# 요청 단위 중복 제거와 재시도

- 관련: [P-001-retry-duplicate](../problems/P-001-retry-duplicate.md) · [P-001-decision](../decisions/P-001-decision.md) · [jsonnode-id-extraction](jsonnode-id-extraction.md)

## 핵심 개념

- **DB 존재 검사만으로는 중복을 막을 수 없는 구간이 있다.** 저장 전(메모리 후보 목록)에 있는 항목은 DB에 없으므로 `existsBy...`가 항상 false다. 같은 요청 안의 중복은 요청 범위의 메모리 구조(`Set`)로 막아야 한다.
- **Set에 키를 넣는 시점이 재시도 가능성을 결정한다.** 실패한 작업을 다시 시도하려면, 실패한 항목이 "처리 완료"로 기록되어서는 안 된다.
- **불변식으로 생각한다.** "저장 후보 목록에서 키는 유일하다", "후보가 된 항목은 다시 변환하지 않는다", "limit은 서로 다른 후보 수로 센다".

## Set 추가 시점 비교 (이 프로젝트의 재시도 루프 기준)

| 시점 | 중복 방지 | 일시 실패 항목 재시도 | 호출 낭비 |
|---|---|---|---|
| 최초 발견 | 됨 | **불가** (이미지 일시 실패 항목이 영영 탈락) | 최소 |
| 원본 유효성 통과 | 됨 | 변환 실패는 가능 | 적음 |
| **후보 목록 추가 (선택)** | 됨 | **가능** | 영구 탈락 항목은 매번 재변환 |

이 코드에서는 "변환 성공"과 "후보 추가"가 사실상 같은 지점이다 (변환 뒤 `isValidSaveMedicineDetail`을 통과해야 후보에 들어간다).

## 이 프로젝트의 적용

- `MedicineServiceImpl.saveMedicinesByCategory`: 루프 밖에서 `Set<String> candidateSplSetIds = new HashSet<>()`을 만들고, 후보 추가 직후에만 `add`한다. 확인 순서는 Set → `existsBySplSetId` → `hasRequiredRawFields` → `SaveConvertToDetail`.
- 요청 단위 변수이므로 스레드 간 공유가 없다. 따라서 `HashSet`으로 충분하다 (동시 요청은 서로 다른 Set을 가진다).

## 검증 [확인: `MedicineServiceImplSaveRetryTest`]

- 같은 ID 후보 중복 없음, A·B 재변환 없음, C 정상 포함.
- 같은 응답 안 중복은 한 번만, limit은 서로 다른 후보 수 기준.
- 변환 후 탈락한 항목은 재시도되고(변환 순서 `[A,B,C,C]`), 성공한 항목은 재변환되지 않는다.
- 서로 다른 약이 limit 미달이면 기존 예외 계약 유지, `saveAll` 미호출.

## 한계 (해결하지 않은 문제)

- **[확인: 설계상]** 요청 단위 Set은 **동시 요청 간 중복을 막지 못한다.** 근본 해결은 DB unique 제약이지만 이번 범위가 아니다 (DDL 확인 불가).
- **[확인: 코드 열람]** 같은 페이지를 반복 요청하는 구조(`skip` 없음)는 그대로다. 재시도가 새 데이터를 가져오지 않는다.
- **[가설]** Set은 대소문자를 구분하지만 DB 조회는 컬럼 collation에 따라 구분하지 않을 수 있다. FDA ID는 소문자 UUID 형태로 오는 것으로 보이나 확인하지 못했다.
- 영구 탈락 항목의 반복 번역·이미지 호출 비용이 남는다 ([backlog](../backlog.md)).
