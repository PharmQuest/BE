# P-001 설계 메모 — splSetId 추출 오류와 재시도 중복 추가

- 상태: 구현 완료, 로컬 커밋됨 (`88b9378`, `0a9c1ca`) (§11 참고)
- 대상: `MedicineServiceImpl.saveMedicinesByCategory`
- 근거 구분: **[확인]** = 테스트 실행으로 확인, **[가설]** = 미확인

## 1. 재현된 두 가지 문제

| | 문제 | 상태 |
|---|---|---|
| A | `result.at("/openfda/spl_set_id").asText("Unknown")`가 배열 응답에서 `""`을 반환한다. 이 값이 `existsBySplSetId`에 전달된다. | **[확인]** |
| B | 재시도 루프가 같은 첫 페이지를 다시 처리하고, DB에 아직 없는 항목이 후보에 중복 추가된다. | **[확인]** |

A는 B의 해결 방식에 영향을 준다. 서비스의 `splSetId` 변수를 Set 키로 쓰면 서로 다른 모든 항목이 `""`로 충돌한다. 따라서 **A가 B의 선행 조건**이다.

## 2. 실패를 입증한 테스트와 결과 (수정 전, 프로덕션 변경 없음)

실행: `JAVA_HOME=<JDK17> ./gradlew test --tests "*MedicineServiceImplSaveRetryTest" --tests "*JsonNodeSplSetIdExtractionTest"`

| 테스트 (`MedicineServiceImplSaveRetryTest`) | 수정 전 결과 | 증거 |
|---|---|---|
| `retry_doesNotAddDuplicateSplSetId` | FAIL | 후보 `["A","B","A"]` |
| `retry_doesNotReconvertSameSplSetId` | FAIL | 변환 대상 `[A,B,A]`, C 누락 |
| `existsCheckReceivesActualSplSetId` | FAIL | `existsBySplSetId("")` 호출 (`MedicineServiceImpl.java:277`) |
| `neverEnoughData_throwsAndDoesNotSave` | FAIL | 서로 다른 약이 2개뿐인데 `[A,B,A]`가 limit 3을 채워 예외 없음 |
| `firstPassSatisfiesLimit_singleCall` | PASS | 정상 경로 |

`JsonNodeSplSetIdExtractionTest` 5개는 모두 PASS (Jackson 및 현재 동작의 특성화).

## 3. 현재 동작과 기대 동작

| | 현재 | 기대 |
|---|---|---|
| ID 추출 | 배열이면 `""` | 배열의 첫 요소 (DTO/컨버터 `getFirstValue`와 같은 규칙) |
| DB 중복 검사 | `existsBySplSetId("")` | `existsBySplSetId(<실제 ID>)` |
| ID 누락·빈 배열·null·공백·`Unknown` | 빈 배열은 이후 `hasRequiredRawFields`에서 NPE로 요청 전체 실패, 나머지는 `""`/`"Unknown"`으로 검사 | 해당 항목만 원본 불량으로 건너뜀(`medicine.raw.invalid.count` 증가), 요청 전체는 실패하지 않음 |
| 재시도 시 같은 ID | 후보에 다시 추가·재변환 | 후보에 이미 있으면 DB 검사·변환 없이 건너뜀 |
| limit 판정 | 중복 포함 건수 | 서로 다른 후보 수 |

## 4. 핵심 불변식

1. 한 요청의 저장 후보 목록에서 `splSetId`는 유일하다.
2. DB 존재 검사에는 실제로 추출한 유효 ID만 전달한다. 유효하지 않은 ID는 검사·변환·저장 대상이 아니다.
3. 후보에 추가된 ID는 같은 요청 안에서 다시 변환(번역·이미지 호출)되지 않는다.
4. 후보에 추가되지 못한 항목(변환 후 검증 실패)은 이후 반복에서 다시 시도할 수 있다.
5. limit 충족 판정은 서로 다른 후보 수 기준이며, 미달 시 기존 예외·`saveAll` 미호출 계약을 유지한다.
6. 1회차에 limit을 채우는 정상 경로의 동작·호출 횟수는 바뀌지 않는다.

## 5. Set에 ID를 추가하는 시점과 이유

| 시점 | 중복 방지 | 일시 실패 항목 재시도 | 호출 낭비 |
|---|---|---|---|
| 최초 발견 | 됨 | **불가** | 최소 |
| 유효성(원본) 통과 | 됨 | 변환 실패는 가능 | 적음 |
| 변환 성공 | 됨 | 가능 | 영구 탈락 항목은 매번 재변환 |
| **후보 목록 추가 (선택)** | 됨 | **가능** | 영구 탈락 항목은 매번 재변환 |

**선택: 후보 목록에 추가하는 시점.** 이유: 불변식 4. 이미지 조회가 일시 실패하면 `null`이 되어 항목이 탈락한다. 최초 발견 시점에 기록하면 그 항목은 이후 반복에서 영영 구제되지 않는다. 잔여 비용(영구 탈락 항목의 반복 변환)은 후속 과제(재시도 정책, skip)로 넘긴다.

## 6. 대안 비교

| 대안 | 장점 | 단점 | 이번 범위 |
|---|---|---|---|
| ID 추출 수정 + 요청 단위 Set (선택) | 변경이 서비스·컨버터에 한정, DB 스키마 불변, 단위 테스트 가능 | 같은 페이지 재요청과 동시 요청 문제는 남음 | **포함** |
| Set + skip 페이지네이션 | 재시도가 새 데이터를 가져옴 | `fetchMedicineData` 시그니처 변경, FDA skip 한도 확인 필요 | 제외 |
| DB unique 제약 | 동시 요청 중복까지 차단 | DDL 접근 필요, 기존 중복 데이터 시 실패, 번역 낭비는 그대로 | 제외 |
| 재시도 정책 변경 | 불필요한 반복 호출 감소 | 동작 계약 변경 | 제외 |

## 7. 정상·경계·실패 시나리오

| 구분 | 시나리오 | 기대 |
|---|---|---|
| 정상 | 1회차에 서로 다른 ID로 limit 충족 | API 1회, 중복 없이 저장 |
| 정상 | 1회차 A,B → 2회차 A,B,C | 후보 `[A,B,C]`, A·B는 1회만 변환 |
| 경계 | 같은 응답 안에 같은 ID 2건 | 1건만 후보 |
| 경계 | ID가 DB에 이미 존재 | `existsBySplSetId(<실제 ID>)` 호출 후 건너뜀 |
| 경계 | ID 필드 누락·빈 배열·null·`""`·공백·`Unknown` | 해당 항목 건너뜀, 요청은 계속 |
| 경계 | 스칼라 문자열 ID | 정상 추출 |
| 경계 | 1회차 C가 변환 후 검증 실패, 2회차에 성공 | C가 후보에 포함 (재시도 보존) |
| 실패 | 서로 다른 유효 ID가 limit 미달 | 기존과 같은 `RuntimeException`, `saveAll` 미호출 |

## 8. 제외한 작업과 잔여 위험 (후속 후보)

- skip 페이지네이션, 재시도 정책 전면 변경, DB unique 제약, 동시 요청 중복, `saveOtherMedicines`, 번역·이미지 실패 처리 정책.
- 영구 탈락 항목이 반복마다 재변환되는 낭비는 남는다.
- `getFirstValue`의 빈 배열 NPE는 `spl_set_id` 외 필드(브랜드명 등)에서 남는다. `hasRequiredRawFields`가 던지면 요청 전체가 실패한다. **[확인: 특성화 테스트]**
- 운영 DB에 이미 쌓인 중복 데이터 여부는 확인하지 못했다. **[가설]** (A 때문에 DB 중복 검사가 동작하지 않았을 가능성)
- A 수정으로 DB 중복 검사가 실제로 동작하기 시작해, 이전에 재저장되던 기존 항목이 건너뛰어지는 **동작 변화**가 있다.

## 9. 수정 예상 파일과 테스트

| 파일 | 변경 |
|---|---|
| `MedicineConverter.java` | 정적 메서드 `extractSplSetId(JsonNode)` 추가 (커밋 1) |
| `MedicineServiceImpl.java` | L274 추출 교체 및 null 처리 (커밋 1), 요청 단위 Set (커밋 2) |
| `MedicineServiceImplSaveRetryTest.java` | 경계·재시도 시나리오 추가 |
| `MedicineConverterExtractSplSetIdTest.java` (신규) | 추출 경계 조건 |
| `JsonNodeSplSetIdExtractionTest.java` | 변경 없음 (특성화 유지) |

정적 메서드로 두는 이유: 서비스 테스트에서 컨버터가 Mock이어도 실제 추출 로직이 실행되므로 Mock 스텁을 늘리지 않고 통합 동작을 검증할 수 있다.

## 10. 롤백 계획

- 커밋 2개로 분리: `git revert <커밋2>`로 Set만, `git revert <커밋1>`로 추출 수정까지 되돌릴 수 있다.
- DB 스키마·데이터·API 응답 계약은 바꾸지 않으므로 데이터 롤백은 없다.

## 11. 구현 결과와 설계 차이

구현은 §3~§9의 설계와 같은 범위(A+B)로 완료했다. 설계와 달라진 점·세부 결정:

| 항목 | 설계 | 구현 | 이유 |
|---|---|---|---|
| 유효하지 않은 ID 처리 | 건너뜀 | 건너뛰고 `medicine.raw.invalid.count` 증가 | 이전에는 ID 누락 시 `hasRequiredRawFields` 실패로 같은 카운터가 증가했으므로 관측 지표를 유지 |
| Set 확인 순서 | Set 확인 후 DB 검사 | Set -> DB -> 원본 필수값 -> 변환 순 | 이미 후보인 항목은 DB 조회조차 하지 않도록 (불변식 3) |
| 커밋 분리 | A, B 두 커밋 | A: 프로덕션 + 컨버터 테스트 / B: Set + `MedicineServiceImplSaveRetryTest` | 서비스 테스트 한 파일에 A·B 시나리오가 함께 있어, A 단계에서는 B 시나리오가 FAIL인 상태로 검증함(아래) |

검증 결과:
- A만 적용: `MedicineServiceImplSaveRetryTest` 7개 중 A 관련 4개(진단 포함) PASS, B 시나리오 3개 FAIL.
- A+B 적용: 신규 포함 30개(`SaveRetry` 9, 컨버터 추출 16, Jackson 특성화 5) 모두 PASS.
- B만 되돌린 상태: `SaveRetry` 9개 중 5개 FAIL (신규 B 테스트 2개 포함) -> 새 테스트가 B를 실제로 검출함.
- 전체 `./gradlew test`: 31개 중 `PharmquestApplicationTests.contextLoads` 1개 FAIL. 원인은 `DataSourceBeanCreationException`(저장소에 `src/main/resources`가 없어 DataSource 설정이 없음). DB 접속은 시도되지 않았다. 이번 변경 이전 상태에서의 동일 실패는 별도로 재실행하지 않았다(원인상 이번 변경과 무관할 가능성이 높으나 미확인).
