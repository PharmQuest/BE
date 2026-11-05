package com.pharmquest.pharmquest.support;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MySQLContainer;

/**
 * 테스트 전용 MySQL 컨테이너. DataSource 연결 정보는 이 컨테이너(@ServiceConnection)에서만 온다.
 *
 * <p>이미지 버전은 운영 MySQL 버전을 확인하지 못해 임의로 고른 값이다. 운영 버전이 확인되면 맞춘다.
 */
@TestConfiguration(proxyBeanMethods = false)
public class MySqlContainerTestConfig {

    public static final String MYSQL_IMAGE = "mysql:8.0";

    @Bean
    @ServiceConnection
    MySQLContainer<?> mysqlContainer() {
        return new MySQLContainer<>(MYSQL_IMAGE);
    }
}
