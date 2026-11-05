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

## 저장 값과의 일치 [확인: 코드 열람 / 테스트로는 미검증]

저장되는 `Medicine.splSetId`는 `MedicineConverter.SaveConvertToDetail`이 `getFirstValue(result, "openfda.spl_set_id")`로 만든 DTO 값이다 (`MedicineServiceImpl.saveMedicinesByCategory`의 `medicine.setSplSetId(dto.getSplSetId())`). `getFirstValue`는 배열이면 첫 요소의 `asText("Unknown")`, 아니면 `asText("Unknown")`이므로, `extractSplSetId`가 null이 아닌 모든 경우에 두 값은 같다.

단, 서비스 테스트에서는 컨버터를 Mock으로 쓰고 DTO를 같은 ID로 만들어 반환하므로 **자동 테스트가 두 값의 일치를 증명하지는 않는다**. 실제 컨버터는 `SaveConvertToDetail`이 필드 주입된 Executor에 의존해 단위 테스트가 어렵다 ([backlog](../backlog.md)).

## 아직 해결하지 않은 문제

- **[확인]** 다른 필드의 빈 배열에서 `getFirstValue`가 NPE를 던진다 (`JsonNodeSplSetIdExtractionTest`의 특성화 테스트가 현재 동작을 고정). `hasRequiredRawFields`에서 발생하면 요청 전체가 실패한다.
- **[확인: 코드 열람]** `saveOtherMedicines`는 여전히 `result.at("/openfda/spl_set_id").asText("Unknown")`를 쓴다.
- **[가설]** 실제 openFDA 응답에서 빈 배열이 오는 빈도.
