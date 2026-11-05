# 백로그 (P-001에서 의도적으로 제외한 후속 문제)

수정하지 않고 기록만 한다. 상태는 모두 "미착수". **[확인]**/**[가설]** 표기는 P-001 시점 기준이다.

| ID | 문제 | 근거 | 영향 | 제안 방향 |
|---|---|---|---|---|
| B-001 | `saveOtherMedicines`의 식별자 추출 패턴 (**P-002에서 처리**: [P-002-save-other-medicines](problems/P-002-save-other-medicines.md)) | **[확인: 코드 열람]** `MedicineServiceImpl.saveOtherMedicines`가 `result.at("/openfda/spl_set_id").asText("Unknown")`를 사용. 같은 컨테이너 노드 문제([jsonnode-id-extraction](concepts/jsonnode-id-extraction.md))가 있을 것으로 추정되나 **실행은 하지 않음** | DB 중복 검사가 `""`로 수행될 수 있음 | `extractSplSetId` 재사용. 이 메서드는 `@Transactional` 안에서 외부 호출·건별 `save()`를 하는 등 별도 정리 필요 |
| B-002 | 다른 필드의 빈 배열 NPE | **[확인]** `JsonNodeSplSetIdExtractionTest` 특성화 테스트: `{"brand_name":[]}`에서 `hasRequiredRawFields`가 NPE. 원인은 `MedicineConverter.getFirstValue`의 `valueNode.get(0).asText(...)` | 요청 전체 실패 | `path(0)` 사용 + 해당 항목만 제외. 고치면 특성화 테스트의 기대값도 의도적으로 바꿔야 함 |
| B-003 | 재시도 시 동일 페이지 반복 요청 | **[확인: 코드 열람]** 루프가 같은 `query`/`requestLimit`으로 재요청하고 `skip`이 없음. P-001은 중복만 막았을 뿐 재시도가 새 데이터를 가져오지는 않음 | 번역·이미지 호출 낭비(영구 탈락 항목), limit 미달 시 3회 반복 후 실패 | `skip` 페이지네이션(FDA `skip` 한도 확인 필요) 또는 재시도 정책 변경 |
| B-004 | DB unique 제약과 동시 요청 중복 | **[가설]** `Medicine.splSetId`에 `@Column(unique=...)`가 없고 DDL은 저장소에서 확인 불가(`src/main/resources` 부재). 요청 단위 Set은 동시 요청을 막지 못함([request-deduplication](concepts/request-deduplication.md)) | 동시 `POST /medicine/save` 시 중복 행 가능, 운영 DB의 기존 중복 데이터 여부 미확인 | 운영 DB의 중복 조사 → unique 제약 + 예외 처리. DDL 접근 필요 |
| B-005 | `contextLoads` 환경 구성 (상세: [test-environment](test-environment.md)) | **[확인]** 변경 전(HEAD)에서도 `PharmquestApplicationTests.contextLoads` 실패. 원인: `DataSourceBeanCreationException: Failed to determine a suitable driver class`(데이터소스 설정 없음). 저장소에 `src/main/resources`와 `src/test/resources`가 없음. 코드가 요구하는 `@Value` 속성 16개(`fda.api.api-key`, `google.cloud.translate.api-key`, `jwt.*`, `cloud.aws.*`, `naver.*`, `openapi.medicine.*`, `place.details.api-key`)도 없음 | 전체 빌드가 항상 실패, CI에서 구분 불가 | 아래 "외부 DB 없이 확인하는 방법" 참고 |
| B-006 | 서비스 테스트가 저장 ID와 추출 ID의 일치를 증명하지 않음 | **[확인: 코드 열람]** 서비스 테스트는 컨버터를 Mock으로 쓰고 DTO를 같은 ID로 반환. 실제 `SaveConvertToDetail`은 필드 주입 Executor(`translationExecutor`, `imageExecutor`)와 번역·이미지 의존이 있어 단위 테스트가 어려움 | `getFirstValue`와 `extractSplSetId`의 규칙이 어긋나도 테스트가 잡지 못함 | 추출 로직을 `getFirstValue`와 공유하도록 리팩터링하거나, 컨버터를 `ReflectionTestUtils`로 구성하는 테스트 추가 |
| B-007 | Set(대소문자 구분)과 DB 조회(collation 의존)의 불일치 | **[가설]** 미확인 | 대소문자만 다른 ID의 중복 처리 | DB collation 확인 후 결정 |

## B-005: 외부 DB 없이 `contextLoads`를 확인하는 방법 (제안, 모두 미검증)

| 방법 | 장점 | 단점 |
|---|---|---|
| H2 인메모리 DB + `src/test/resources/application.yml` | 가장 간단 | **새 테스트 의존성 승인 필요**, MySQL 전용 SQL·`columnDefinition = "TEXT"` 호환 확인 필요 |
| Testcontainers MySQL | 운영과 같은 DB | 새 의존성 + Docker 필요 (이 PC에는 Docker Desktop이 있음) |
| 슬라이스 테스트 (`@WebMvcTest` 등)로 대체 | DB 불필요 | 전체 컨텍스트 검증은 아님 |
| 프로퍼티만 더미로 채우기 | 의존성 추가 없음 | JPA 리포지토리가 있어 DataSource 없이 컨텍스트 구성 불가 → 그대로는 실패할 가능성이 높음 |

어떤 방법이든 `contextLoads`를 `@Disabled`로 숨기지 않는다.
