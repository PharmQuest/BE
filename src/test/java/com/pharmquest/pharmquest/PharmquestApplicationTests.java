package com.pharmquest.pharmquest;

import com.pharmquest.pharmquest.support.MySqlContainerTestConfig;
import com.pharmquest.pharmquest.support.RequiresDocker;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MySQLContainer;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 통합 테스트: Docker가 필요하다. Docker를 쓸 수 없으면 SKIPPED로 보고되고(성공이 아님),
 * 환경변수 REQUIRE_DOCKER=true이면 SKIP하지 않고 실패한다 (CI 권장).
 * 실행: ./gradlew test --tests "*PharmquestApplicationTests"
 */
@RequiresDocker
@SpringBootTest
@ActiveProfiles("test")
@Import(MySqlContainerTestConfig.class)
class PharmquestApplicationTests {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private MySQLContainer<?> mysql;

	@Test
	void contextLoads() {
	}

	@Test
	void dataSourceUsesTestcontainerOnly() throws Exception {
		String url = dataSource.unwrap(HikariDataSource.class).getJdbcUrl();

		// 운영/개발 DB 주소가 섞였다면 컨테이너 주소와 달라 여기서 실패한다.
		assertThat(url).isEqualTo(mysql.getJdbcUrl());
		assertThat(url).startsWith("jdbc:mysql://");
		assertThat(java.net.URI.create(url.substring("jdbc:".length())).getHost())
				.isIn("localhost", "127.0.0.1", mysql.getHost());
	}

}
