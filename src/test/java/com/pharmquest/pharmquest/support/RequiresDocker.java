package com.pharmquest.pharmquest.support;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Docker가 필요한 통합 테스트 표시.
 *
 * <ul>
 *   <li>Docker를 사용할 수 없고 {@code REQUIRE_DOCKER}가 설정되지 않은 경우: SKIPPED (사유 포함)</li>
 *   <li>{@code REQUIRE_DOCKER=true}(CI 권장)인데 Docker를 사용할 수 없는 경우: <b>FAILED</b></li>
 * </ul>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Tag("integration")
@ExtendWith(DockerAvailableCondition.class)
public @interface RequiresDocker {
}
