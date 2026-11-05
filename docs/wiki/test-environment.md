# 테스트 환경 현황과 `contextLoads` 실패 (backlog B-005)

- 관련: [backlog](backlog.md) · [P-001-retry-duplicate](problems/P-001-retry-duplicate.md) · [AGENTS.md](../../AGENTS.md)
- 상태: **기록만 함. 환경은 수정하지 않았다.**

## 현재 확인된 사실

| 항목 | 내용 | 근거 |
|---|---|---|
| 실패 테스트 | `PharmquestApplicationTests.contextLoads` | 전체 테스트 실행 [확인] |
| 직접 원인 | `DataSourceBeanCreationException: Failed to determine a suitable driver class` (데이터소스 설정 없음) | 스택 트레이스 [확인] |
| 변경 전에도 실패 | P-001 변경 전 HEAD `a43e57b`를 별도 worktree에서 실행해도 같은 원인으로 실패 | P-001 [확인] |
| 설정 파일 | 저장소에 `src/main/resources`, `src/test/resources`가 없다. 코드가 요구하는 `@Value` 속성도 16개 모두 없다 | 파일 조사·코드 열람 [확인] |
| DB 연결 시도 | 일어나지 않음 (드라이버 결정 단계에서 실패) | 스택 트레이스 [확인] |
| 드라이버 | 런타임 의존성은 MySQL(`mysql-connector-j`)뿐이고 인메모리 DB 의존성은 없다 | `build.gradle` [확인] |

전체 45개 중 `contextLoads`를 제외한 44개는 Spring 컨텍스트 없이 실행되어 통과했다 (P-001 관련 30개 + P-002 14개를 포함, 2026-11 기준 로컬 실행).

## CI와의 관계

- `.github/workflows/dev_deploy.yml`은 `main`에 push될 때 `./gradlew build`를 실행하고, 그 전에 `secrets.APPLICATION_YML`로 `src/main/resources/application.yml`을 생성한다 [확인: 워크플로 파일].
- 따라서 CI에서 `contextLoads`는 **로컬과 다른 조건**으로 실행된다. 그 결과(통과 여부, 시크릿이 가리키는 DB 종류와 접근 가능 여부)는 **확인하지 못했다 [가설]**.
- CI가 시크릿 DB에 연결해 통과하는 구조라면 테스트가 외부 상태에 의존하는 것이므로, 환경 개선 시 이를 대체할지 판단이 필요하다.

## 해결 대안 (모두 미검증, 환경 변경은 승인 필요)

| 대안 | 새 의존성 | DB 구성 | 장단점 |
|---|---|---|---|
| A. H2 인메모리 (MySQL이 아닌 다른 DB 엔진을 흉내 냄) + `src/test/resources/application.yml` | H2 (테스트 전용) | 불필요 | 가장 간단. MySQL 전용 문법·`columnDefinition = "TEXT"` 호환 확인 필요. 16개 속성은 더미 값 |
| B. Testcontainers MySQL (Docker로 실제 MySQL을 띄움) | Testcontainers | Docker 필요 | 운영과 동일한 DB. 느리고 Docker가 없는 환경(일부 CI)에서는 불가 |
| C. 슬라이스 테스트로 대체 (`@WebMvcTest` 등) | 없음 | 불필요 | 전체 컨텍스트 로딩 검증은 사라진다 |
| D. 더미 프로퍼티만 추가 | 없음 | 불필요 | DataSource가 없어 JPA 리포지토리 때문에 실패할 가능성이 높다 |

`contextLoads`를 `@Disabled`로 숨기는 것은 대안이 아니다 (실패 은폐).

## 향후 CI 적용 시 고려

1. 현재 CI의 시크릿 기반 `application.yml`과 테스트용 설정을 **분리**한다 (`src/test/resources`는 테스트 전용).
2. 테스트가 운영·개발 DB와 외부 API(FDA, 번역, 이미지)에 닿지 않게 한다.
3. 번역·이미지용 `Executor` 빈과 `@Value` 속성 16개를 테스트에서 어떻게 공급할지 정한다.
4. 새 의존성을 추가하면 CI 캐시 삭제 단계(`rm -rf ~/.gradle/caches`) 때문에 매번 다운로드된다. 빌드 시간 영향을 확인한다.
5. 전체 `build`가 이 테스트 하나 때문에 항상 실패하면, 새 회귀를 실패로 구분할 수 없다. 해결 전까지는 기존 실패 이름을 보고서에 명시한다.

## 승인이 필요한 사항

- 새 테스트 의존성 (A, B)
- `src/test/resources` 설정 추가
- CI 워크플로 변경
