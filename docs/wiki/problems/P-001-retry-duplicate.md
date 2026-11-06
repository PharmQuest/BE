# P-001 재시도 루프의 중복 추가와 splSetId 추출 오류

- 상태: 구현·테스트 완료, 로컬 커밋됨 (push 전)
- 커밋: A `88b9378` (splSetId 추출 수정) · B `0a9c1ca` (재시도 중복 방지). 문서 커밋의 해시는 자기 참조를 피하려고 기록하지 않는다.
- 관련: [jsonnode-id-extraction](../concepts/jsonnode-id-extraction.md) · [request-deduplication](../concepts/request-deduplication.md) · [P-001-decision](../decisions/P-001-decision.md) · [backlog](../backlog.md)
- 대상 코드: `MedicineServiceImpl.saveMedicinesByCategory` (`POST /medicine/save`)

## 문제 요약

| | 문제 | 상태 |
|---|---|---|
| A | `result.at("/openfda/spl_set_id").asText("Unknown")`가 배열 응답에서 `""`을 반환하고, 그 값이 `existsBySplSetId`에 전달된다. | **[확인]** |
| B | 재시도 루프가 같은 첫 페이지를 다시 처리하여, 아직 DB에 없는 같은 항목이 저장 후보에 중복 추가된다. | **[확인]** |

A가 B 해결의 선행 조건이다. 요청 단위 Set의 키로 A의 값을 쓰면 서로 다른 모든 항목이 `""`로 충돌한다.

## 재현 조건 [확인]

limit=3, 외부 API와 DB는 Mockito로 대체.

| 호출 | `fetchMedicineData` 반환 |
|---|---|
| 1회차 | `spl_set_id` 배열로 A, B (유효 2건 < 3) |
| 2회차 | A, B, C |

`existsBySplSetId`는 항상 false (기존 DB에 없음).

## 기존 코드가 실패한 이유 [확인: 코드 추적 + 테스트]

1. 루프는 매 반복 같은 `query`와 `requestLimit`으로 첫 페이지를 다시 가져온다 (`skip` 없음).
2. 1회차에 후보로 담은 항목은 아직 저장 전이라 DB 검사에 걸리지 않는다.
3. 2회차에 A가 다시 후보에 추가되어 크기가 limit(3)에 도달해 루프가 끝난다. 결과는 `[A,B,A]`이고 C는 누락된다.
4. 서로 다른 약이 2개뿐이어도 중복이 limit을 채워 예외 없이 저장을 시도한다 (실패 계약 위반).
5. A의 영향: 배열 `spl_set_id`에서 `asText("Unknown")`은 `""`이라, DB 검사가 항상 `existsBySplSetId("")`로 수행된다.

## 수정 전 테스트 결과 [확인]

| 테스트 (`MedicineServiceImplSaveRetryTest`) | 수정 전 | 증거 |
|---|---|---|
| `retry_doesNotAddDuplicateSplSetId` | FAIL | 후보 `["A","B","A"]` |
| `retry_doesNotReconvertSameSplSetId` | FAIL | 변환 `[A,B,A]`, C 누락 |
| `existsCheckReceivesActualSplSetId` | FAIL | `existsBySplSetId("")` 호출 |
| `neverEnoughData_throwsAndDoesNotSave` | FAIL | 예외 없음 |
| `firstPassSatisfiesLimit_singleCall` | PASS | 정상 경로 |

## 수정 후 검증 결과 [확인]

- P-001 관련 30개 모두 PASS: `MedicineServiceImplSaveRetryTest` 9, `MedicineConverterExtractSplSetIdTest` 16, `JsonNodeSplSetIdExtractionTest` 5.
- B 변경만 되돌린 상태에서 `MedicineServiceImplSaveRetryTest` 9개 중 5개 FAIL (신규 B 테스트 2개 포함). 새 테스트가 B를 실제로 검출한다.
- 전체 `./gradlew test`: 31개 중 30 PASS, 1 FAIL (`PharmquestApplicationTests.contextLoads`). 이 실패는 변경 전(HEAD) 커밋에서도 동일하게 재현된다 ([backlog](../backlog.md) 참고).

## 커밋 단위 검증 [확인]

| 시점 | 테스트 | 결과 |
|---|---|---|
| 커밋 A `88b9378` 단독 (임시 worktree에서 B 제외) | 추출 16 + 특성화 5 | 21 PASS |
| 같은 상태에서 `SaveRetry` 9 | 4 PASS, 5 FAIL (B 미적용이므로 예상된 결과) | |
| 커밋 B `0a9c1ca` 이후 | P-001 관련 30개 | 모두 PASS |
| 커밋 B 이후 전체 | 31개 | 30 PASS, `contextLoads` 1 FAIL (HEAD `a43e57b`에서도 동일) |

## 가설 (미검증)

- **[가설]** 운영 DB에는 A 때문에 같은 `splSetId`의 중복 행이 이미 있을 수 있다. 실제 DB와 스키마를 확인하지 못했다.
- **[가설]** 실제 openFDA 응답에서 `limit*3` 요청의 첫 페이지가 유효 항목으로 limit에 못 미치는 빈도. 이 빈도가 낮으면 B의 실제 발생률도 낮다.

## 해결하지 않은 문제

[backlog](../backlog.md) 참고: `saveOtherMedicines` 동일 패턴, 다른 필드의 빈 배열 NPE, 같은 페이지 반복 요청, DB unique 제약과 동시 요청, `contextLoads` 환경.

## 회고

- 호출 횟수 총합 검증은 현재 코드(A,B,A)도 우연히 통과했다. ID별 검증으로 바꿔서 잡았다.
- 사전 예측이 틀린 곳이 있었다: "미달이면 예외" 회귀 테스트를 통과 예상으로 분류했지만 같은 중복 버그 때문에 FAIL했다. 테스트 분류는 실행 전에 단정하지 않는다.
- 로컬에 JDK 17이 없어 첫 실행이 막혔다. Temurin 17을 공식 배포처에서 받아 체크섬을 확인한 뒤 사용자 폴더에 압축 해제했다.
