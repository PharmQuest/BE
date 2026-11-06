package com.pharmquest.pharmquest;

import com.pharmquest.pharmquest.support.MySqlContainerTestConfig;
import com.pharmquest.pharmquest.support.RequiresDocker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * B-009: 테스트가 src/test/resources/application.yml의 더미 설정만 쓰고,
 * main의 application.yml(CI의 APPLICATION_YML 시크릿 등)이나 실제 키가 유입되지 않는지 확인한다.
 *
 * <p>main에 같은 키가 있어도 테스트 값이 이겨야 한다. 실제 값은 이 파일에 쓰지 않는다.
 * Docker가 필요한 통합 테스트다 (PharmquestApplicationTests와 같은 컨텍스트를 재사용한다).
 */
@RequiresDocker
@SpringBootTest
@ActiveProfiles("test")
@Import(MySqlContainerTestConfig.class)
class TestConfigIsolationTests {

	/** src/test/resources/application.yml에 정의한 더미 값. 하나라도 다르면 다른 설정이 유입된 것이다. */
	private static final Map<String, String> EXPECTED_DUMMIES = Map.ofEntries(
			Map.entry("jwt.secret", "dGVzdC1vbmx5LXNlY3JldC1rZXktZm9yLXVuaXQtdGVzdHMtMDAwMDAwMDA="),
			Map.entry("jwt.redirect", "http://localhost.invalid/redirect"),
			Map.entry("cloud.aws.credentials.access-key", "test-access-key"),
			Map.entry("cloud.aws.credentials.secret-key", "test-secret-key"),
			Map.entry("cloud.aws.region.static", "ap-northeast-2"),
			Map.entry("cloud.aws.s3.bucket", "test-bucket"),
			Map.entry("google.cloud.translate.api-key", "test-api-key"),
			Map.entry("fda.api.api-key", "test-api-key"),
			Map.entry("openapi.medicine.base-url", "http://localhost.invalid"),
			Map.entry("openapi.medicine.api-key", "test-api-key"),
			Map.entry("naver.api.url", "http://localhost.invalid"),
			Map.entry("naver.client.id", "test-client-id"),
			Map.entry("naver.client.secret", "test-client-secret"),
			Map.entry("place.details.api-key", "test-api-key"),
			Map.entry("spring.jpa.hibernate.ddl-auto", "create-drop")
	);

	@Autowired
	private Environment env;

	@Autowired
	private ClientRegistrationRepository clientRegistrations;

	@Test
	void testProfileIsActivatedExplicitly() {
		assertThat(env.getActiveProfiles()).containsExactly("test");
	}

	@Test
	void testDummyValuesWinOverAnyOtherApplicationYml() {
		EXPECTED_DUMMIES.forEach((key, expected) ->
				assertThat(env.getProperty(key)).as("설정 키 %s", key).isEqualTo(expected));
	}

	@Test
	void noProfileSpecificFileFromMainLeaksIn() throws IOException {
		// application-test.yml 같은 프로파일 파일은 test 쪽 application.yml을 대체하지 않고 위에 덮어쓰므로 별도로 막는다.
		List<String> found = new ArrayList<>();
		for (String name : List.of("application-test.yml", "application-test.yaml", "application-test.properties")) {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			loader.getResources(name).asIterator().forEachRemaining(url -> found.add(url.toString()));
		}
		assertThat(found).as("테스트 프로파일 설정 파일이 클래스패스에 있으면 더미 설정과 섞인다").isEmpty();
	}

	@Test
	void oauthRegistrationsAreTestDummiesOnly() {
		List<ClientRegistration> registrations = new ArrayList<>();
		((Iterable<ClientRegistration>) clientRegistrations).forEach(registrations::add);

		assertThat(registrations).extracting(ClientRegistration::getRegistrationId).containsExactly("test");
		ClientRegistration test = registrations.get(0);
		assertThat(test.getClientId()).isEqualTo("test-client-id");
		assertThat(test.getProviderDetails().getAuthorizationUri()).startsWith("http://localhost.invalid/");
		assertThat(test.getProviderDetails().getTokenUri()).startsWith("http://localhost.invalid/");
	}

}
