# 어디약 LLM Wiki 인덱스

개발 경험을 `문제 → 개념 → 설계 결정 → 실제 코드 → 테스트 → 검증 결과 → 회고` 순서로 연결해 두는 문서 모음이다.

## 작성 규칙

- **[확인]** 테스트 실행 또는 코드 열람으로 직접 확인한 사실, **[가설]** 아직 검증하지 못한 내용. 두 가지를 섞어 쓰지 않는다.
- 코드는 라인 번호 대신 `클래스.메서드`로 가리킨다 (라인은 쉽게 어긋난다).
- 기존 성능 개선 서술은 복제하지 않고 [README.md](../../README.md)를 참조한다. 해당 내용은 README의 "핵심 성과", "재현 가능한 성능 검증", "문제 해결과 의사결정"에 있다.

## 문제 (problems)

| ID | 제목 | 상태 | 문서 |
|---|---|---|---|
| P-001 | `saveMedicinesByCategory` 재시도 루프의 중복 추가와 splSetId 추출 오류 | 구현·테스트 완료, 로컬 커밋됨 (`88b9378`, `0a9c1ca`) | [P-001-retry-duplicate](problems/P-001-retry-duplicate.md) |

## 개념 (concepts)

| 개념 | 문서 |
|---|---|
| Jackson `JsonNode`에서 식별자 추출 (`asText` 함정) | [jsonnode-id-extraction](concepts/jsonnode-id-extraction.md) |
| 요청 단위 중복 제거와 재시도 | [request-deduplication](concepts/request-deduplication.md) |

## 설계 결정 (decisions)

| 결정 | 문서 |
|---|---|
| P-001 설계 결정 (ID 추출 수정 + 후보 추가 시점 Set) | [P-001-decision](decisions/P-001-decision.md) |

## 백로그

- [backlog](backlog.md): P-001에서 의도적으로 제외한 후속 문제

## 관련 원본 문서

- 설계 메모 초안(작업 로그 성격): [docs/ai-workflow/P-001-design-memo.md](../ai-workflow/P-001-design-memo.md)
- 프로젝트 소개와 성능 개선 기록: [README.md](../../README.md)

## 연결 흐름 (P-001)

```
문제 P-001
  ├─ 개념: jsonnode-id-extraction  ──┐
  ├─ 개념: request-deduplication   ──┤
  └─ 결정: P-001-decision ───────────┴─> 코드(MedicineConverter.extractSplSetId, MedicineServiceImpl.saveMedicinesByCategory)
                                          └─> 테스트 3개 파일 ─> 검증 결과 ─> 백로그
```
