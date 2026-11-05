# P-001 설계 결정

- 관련: [P-001-retry-duplicate](../problems/P-001-retry-duplicate.md) · [jsonnode-id-extraction](../concepts/jsonnode-id-extraction.md) · [request-deduplication](../concepts/request-deduplication.md) · [backlog](../backlog.md)
- 상세 작업 로그: [P-001-design-memo.md](../../ai-workflow/P-001-design-memo.md)
- 성능 개선(번역 병렬화, 트랜잭션 분리 등)의 배경은 [README.md](../../../README.md)를 참조하며 이 문서에서 반복하지 않는다.

## 결정

1. **ID 추출을 `MedicineConverter.extractSplSetId` 정적 메서드로 분리**하고 서비스가 이를 사용한다.
2. **`saveMedicinesByCategory`에 요청 단위 `Set<String> candidateSplSetIds`를 도입**하고, **후보 목록에 추가에 성공한 시점**에만 기록한다.
3. 유효하지 않은 ID는 요청 전체를 실패시키지 않고 항목만 건너뛰며 `medicine.raw.invalid.count`를 증가시킨다.

## 고려한 대안

| 대안 | 해결 | 못 푸는 것 | 판단 |
|---|---|---|---|
| ID 추출 수정 + 요청 단위 Set (선택) | 요청 내 중복·재변환 | 같은 페이지 재요청, 동시 요청 | **P-001 채택** |
| Set + skip 페이지네이션 | 재시도가 새 데이터를 가져옴 | 동시 요청 | 시그니처 변경·FDA skip 한도 확인 필요 → 제외 |
| DB unique 제약 | 동시 요청 중복까지 | 번역·이미지 낭비, 기존 중복 데이터 시 DDL 실패 | DDL 접근 불가 → 제외 |
| 재시도 정책 변경 | 불필요한 반복 호출 | 중복 방어 자체 | 동작 계약 변경 → 제외 |

## 선택 이유

- **A가 B의 선행 조건**이다. 서비스의 기존 `splSetId` 변수를 키로 쓰면 모든 항목이 `""`로 충돌한다.
- **Set 기록 시점은 "후보 추가 후"**: "최초 발견" 시점에 기록하면 이미지 조회가 일시 실패한 항목이 영영 재시도되지 않는다 ([request-deduplication](../concepts/request-deduplication.md) 비교표).
- **정적 메서드**: 서비스 테스트에서 컨버터가 Mock이어도 실제 추출 로직이 실행되므로 스텁을 늘리지 않고 통합 동작을 검증할 수 있다.
- 지표를 유지한 이유: 이전에도 ID 누락 시 `hasRequiredRawFields` 실패로 같은 카운터가 증가했다.

## 변경한 클래스·메서드

| 위치 | 변경 |
|---|---|
| `MedicineConverter.extractSplSetId(JsonNode)` | 신규 (정적) |
| `MedicineServiceImpl.saveMedicinesByCategory` | 추출 교체 + null 건너뛰기 (커밋 `88b9378`), `candidateSplSetIds` Set (커밋 `0a9c1ca`) |

변경하지 않은 것 **[확인: diff]**: `CompletableFuture` 병렬 변환, `Timer` 계측, `transactionTemplate.execute(... saveAll ...)` 트랜잭션 범위, `saveOtherMedicines`, 컨트롤러 응답 계약.

## 동작 변화 (의도된 것)

DB 중복 검사가 실제 ID로 수행된다. 이전에는 `""`로 검사되어 이미 저장된 약도 다시 저장 대상이 될 수 있었다 (**[가설]**: 실제 DB의 중복 데이터 존재 여부는 미확인).

## 검증 범위

| 항목 | 결과 |
|---|---|
| `MedicineServiceImplSaveRetryTest` 9 | 모두 PASS **[확인]** |
| `MedicineConverterExtractSplSetIdTest` 16 | 모두 PASS **[확인]** |
| `JsonNodeSplSetIdExtractionTest` 5 (특성화) | 모두 PASS **[확인]** |
| 변경 A만 적용 | A 관련 테스트 PASS, B 시나리오 3개 FAIL **[확인]** |
| 변경 B 제거 | `SaveRetry` 9개 중 5개 FAIL **[확인]** |
| 전체 테스트 31 | 30 PASS, `contextLoads` 1 FAIL (변경 전에도 동일 실패) **[확인]** |

검증하지 못한 것: 실제 openFDA/번역/이미지 API와 실제 DB를 사용한 end-to-end 동작, 동시 요청, 실제 컨버터로 저장 ID와 추출 ID의 일치(코드 열람으로만 확인).

## 해결하지 않은 문제

[backlog](../backlog.md) 참고.
