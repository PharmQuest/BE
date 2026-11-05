package com.pharmquest.pharmquest.support;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.DockerClientFactory;

/** {@link RequiresDocker}의 판정 로직. Docker 사용 불가 시 SKIP 또는(REQUIRE_DOCKER=true) FAIL. */
class DockerAvailableCondition implements ExecutionCondition {

    static final String REQUIRE_DOCKER_ENV = "REQUIRE_DOCKER";

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        boolean available;
        try {
            available = DockerClientFactory.instance().isDockerAvailable();
        } catch (Throwable t) {
            available = false;
        }
        if (available) {
            return ConditionEvaluationResult.enabled("Docker 사용 가능");
        }

        String reason = "Docker를 사용할 수 없어 통합 테스트를 실행하지 못했다 (Docker 미실행 또는 클라이언트-데몬 API 비호환)";
        if ("true".equalsIgnoreCase(System.getenv(REQUIRE_DOCKER_ENV))) {
            throw new IllegalStateException(reason + ". " + REQUIRE_DOCKER_ENV + "=true 이므로 SKIP하지 않고 실패 처리한다.");
        }
        return ConditionEvaluationResult.disabled("NOT RUN: " + reason);
    }
}
