# Jackson JsonNode에서 식별자 추출 (asText 함정)

- 관련: [P-001-retry-duplicate](../problems/P-001-retry-duplicate.md) · [P-001-decision](../decisions/P-001-decision.md) · [request-deduplication](request-deduplication.md)

## 핵심 개념

- openFDA의 `openfda.*` 필드(`spl_set_id`, `brand_name` 등)는 **배열**로 온다.
- Jackson의 `JsonNode.asText(defaultValue)`는 값 노드에서만 의미 있는 값을 준다. 배열·객체 같은 컨테이너 노드에서는 첫 요소가 아니라 **빈 문자열**을 반환한다. 기본값(`"Unknown"`)이 쓰이는 것은 필드가 없는 MissingNode의 경우로 확인했다(null 노드는 미검증).
- 따라서 배열에는 `node.path(0)` 또는 `node.get(0)`으로 요소를 꺼낸 뒤 읽어야 한다.

## 동작 비교 [확인: `JsonNodeSplSetIdExtractionTest`]

| 입력 | `at("/openfda/spl_set_id").asText("Unknown")` |
|---|---|
| `["abc-123"]` (배열) | `""` |
| `"abc-123"` (스칼라) | `"abc-123"` |
| 필드 없음 | `"Unknown"` |

같은 `["A"]`와 `["B"]`가 모두 `""`이 되어 식별자로서 서로 구분되지 않는다.

## 안전한 읽기 패턴

| 메서드 | 빈 배열 | 비고 |
|---|---|---|
| `node.get(0)` | `null` 반환 → 이후 `.asText()`에서 NPE | 기존 `MedicineConverter.getFirstValue`가 이 패턴 (아래 NPE 항목) |
| `node.path(0)` | `MissingNode` 반환 (NPE 없음) | `extractSplSetId`가 사용 |

## 이 프로젝트의 적용

- 추출 규칙 구현: `MedicineConverter.extractSplSetId(JsonNode)` (정적 메서드).
  - 배열이면 첫 요소, 스칼라이면 그대로.
  - 필드 누락·빈 배열·null·`[null]`·빈 문자열·공백·`Unknown`(대소문자 무시)·객체이면 `null`.
  - 값을 trim하거나 변환하지 않는다 (DB에 저장되는 값과 같은 규칙이어야 하므로).
- 경계 조건 테스트 16개: `MedicineConverterExtractSplSetIdTest` **[확인: PASS]**.

## 저장 값과의 일치 [확인: 코드 열람 / 컨버터 수준은 아래 B-006 절에서 테스트로 확인, 서비스 연결은 미검증]

저장되는 `Medicine.splSetId`는 `MedicineConverter.SaveConvertToDetail`이 `getFirstValue(result, "openfda.spl_set_id")`로 만든 DTO 값이다 (`MedicineServiceImpl.saveMedicinesByCategory`의 `medicine.setSplSetId(dto.getSplSetId())`). `getFirstValue`는 배열이면 첫 요소의 `asText("Unknown")`, 아니면 `asText("Unknown")`이므로, `extractSplSetId`가 null이 아닌 모든 경우에 두 값은 같다.

단, 서비스 테스트에서는 컨버터를 Mock으로 쓰고 DTO를 같은 ID로 만들어 반환하므로 **자동 테스트가 두 값의 일치를 증명하지는 않는다**. 실제 컨버터는 `SaveConvertToDetail`이 필드 주입된 Executor에 의존해 단위 테스트가 어렵다 ([backlog](../backlog.md)).

## 실제 컨버터 일치 검증 (B-006)

상태: **부분 검증 완료**. 테스트: `MedicineConverterSplSetIdParityTest` (13개 **[확인: PASS]**). 실제 `MedicineConverter`로 `SaveConvertToDetail(node, null)`을 실행하고, 번역(입력 그대로 반환)·이미지 조회(null 반환)·Executor(`Runnable::run`, `ReflectionTestUtils`로 주입)만 대체했다. DTO를 테스트가 만들어 주입하지 않는다.

### 정상 입력: 추출값과 DTO `splSetId`가 일치 [확인]

| 입력 | `extractSplSetId` | DTO |
|---|---|---|
| `["abc"]` | `abc` | `abc` |
| `"abc"` | `abc` | `abc` |
| `[" Ab-1 "]` | `" Ab-1 "` | `" Ab-1 "` (양쪽 모두 trim하지 않음) |
| `["A","B"]` | `A` | `A` (첫 요소) |

### 무효 입력: 실제 컨버터가 내는 값 [확인: 관찰, 테스트가 고정하지 않음]

| 입력 | `extractSplSetId` | 실제 `SaveConvertToDetail` |
|---|---|---|
| 필드 누락 / `null` / `[null]` / `"Unknown"` | `null` | DTO `"Unknown"` |
| 공백 `"   "` | `null` | DTO `"   "` (공백 그대로) |
| 객체 `{"a":1}` | `null` | DTO `""` |
| `["","x"]` | `null` | DTO `""` (첫 요소만 봄) |
| 빈 배열 `[]` | `null` | **`NullPointerException`** (`getFirstValue`의 `get(0)`, B-002와 같은 원인) |

테스트가 단언하는 것은 "무효 입력에서 `extractSplSetId`가 `null`"뿐이고, 컨버터의 결과·예외는 출력만 한다. 현재의 `Unknown`/공백/빈 문자열/NPE 동작을 정상으로 고정하지 않는다.

### 서비스 사전 필터가 필요한 이유

컨버터 자체는 무효 ID를 거부하지 않는다. `"Unknown"`, 공백, 빈 문자열이 저장 가능한 값처럼 DTO에 담기고, 빈 배열은 예외를 던진다. `isValidSaveMedicineDetail`도 `splSetId`를 검사하지 않는다. 따라서 `extractSplSetId == null`인 항목을 **변환 이전에** 걸러내는 서비스 필터(P-001/P-002)가 유일한 방어선이다.

### 증명한 범위와 못 한 범위

- **증명**: 정상 4종에서 컨버터 내부의 추출 규칙과 저장 DTO 규칙이 같다. 필터가 거르는 무효 입력의 목록.
- **미증명**: 서비스가 추출한 ID로 DB를 조회하고 **같은 값**을 `Medicine`에 저장하는 연결(서비스 테스트는 컨버터가 Mock). 실제 DB 컬럼에 저장된 값(길이·collation, B-007), JPA flush·커밋. 번역·이미지·스레드풀의 실제 동작.
- **위험**: 두 규칙은 여전히 별개 구현이다. 한쪽만 바뀌면 이 테스트의 정상 입력 단언이 깨져 감지하지만, 테스트 표에 없는 입력은 잡지 못한다. 일원화(B안)는 별도 설계 결정 대상이다.

## 아직 해결하지 않은 문제

- **[확인]** 다른 필드의 빈 배열에서 `getFirstValue`가 NPE를 던진다 (`JsonNodeSplSetIdExtractionTest`의 특성화 테스트가 현재 동작을 고정). `hasRequiredRawFields`에서 발생하면 요청 전체가 실패한다.
- **[정정]** `saveOtherMedicines`의 식별자 추출 ([P-002](../problems/P-002-save-other-medicines.md), 커밋 `fbd33cc`):
  - P-002 이전: `result.at("/openfda/spl_set_id").asText("Unknown")`을 사용했다 (위 표의 컨테이너 노드 문제 대상) [확인: `fbd33cc` diff].
  - 현재: `MedicineConverter.extractSplSetId(result)`를 재사용하고, 무효 ID(`null`)는 건너뛴다 [확인: 현재 코드 `MedicineServiceImpl.saveOtherMedicines`].
  - 이 항목은 해결되었다 (backlog B-001).
- **[가설]** 실제 openFDA 응답에서 빈 배열이 오는 빈도.
