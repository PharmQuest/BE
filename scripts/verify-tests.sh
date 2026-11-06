#!/usr/bin/env bash
# 통합 테스트가 실제로 실행됐는지 확인한다 (dev_deploy.yml, pr_test.yml 공용).
#
# Gradle이 성공해도 Docker를 쓸 수 없으면 통합 테스트가 SKIPPED로 보고될 수 있다(종료코드 0).
# 그 경우를 성공으로 오인하지 않도록 테스트 리포트(JUnit XML)를 직접 검사한다.
#
# 사용법: bash scripts/verify-tests.sh [테스트 결과 디렉터리]   (기본: build/test-results/test)
# 종료코드: 0 = 모두 충족, 1 = 하나라도 실패 (원인을 모두 출력한다)

set -uo pipefail

RESULTS_DIR="${1:-build/test-results/test}"
PACKAGE="com.pharmquest.pharmquest"
# 반드시 실행되어야 하는 통합 테스트 클래스. 클래스 이름·패키지를 바꾸면 여기도 같이 고친다.
REQUIRED_CLASSES=(PharmquestApplicationTests TestConfigIsolationTests)

failed=0
fail() {
  echo "ERROR: $*" >&2
  failed=1
}

# 첫 <testsuite ...> 태그에서 숫자 속성 값을 꺼낸다. 없으면 빈 문자열.
suite_attr() { # $1=file $2=attribute
  grep -m1 -o '<testsuite [^>]*>' "$1" | sed -n "s/.* $2=\"\([0-9][0-9]*\)\".*/\1/p"
}

if [ ! -d "$RESULTS_DIR" ]; then
  fail "테스트 결과 디렉터리가 없다: $RESULTS_DIR (gradle test가 실행되지 않았거나 결과 경로가 다르다. 작업 디렉터리: $(pwd))"
  exit 1
fi

shopt -s nullglob
all_reports=("$RESULTS_DIR"/*.xml)
if [ "${#all_reports[@]}" -eq 0 ]; then
  fail "테스트 리포트(*.xml)가 하나도 없다: $RESULTS_DIR (테스트가 실행되기 전에 빌드가 실패했을 수 있다)"
  exit 1
fi

echo "== 필수 통합 테스트 클래스 =="
for cls in "${REQUIRED_CLASSES[@]}"; do
  report="$RESULTS_DIR/TEST-$PACKAGE.$cls.xml"
  if [ ! -f "$report" ]; then
    fail "$cls: 리포트가 없다 ($report). 원인 후보: 클래스 이름/패키지 변경, 테스트 필터(--tests) 사용, 컨텍스트 로드 이전 실패. 존재하는 리포트: $(printf '%s ' "${all_reports[@]##*/}")"
    continue
  fi

  tests="$(suite_attr "$report" tests)"
  skipped="$(suite_attr "$report" skipped)"
  failures="$(suite_attr "$report" failures)"
  errors="$(suite_attr "$report" errors)"

  if [ -z "$tests" ] || [ -z "$skipped" ]; then
    fail "$cls: 리포트 형식을 읽을 수 없다 ($report). <testsuite> 태그의 tests/skipped 속성이 없다"
    continue
  fi
  echo "  $cls: tests=$tests skipped=$skipped failures=${failures:-?} errors=${errors:-?}"

  if [ "$tests" -lt 1 ]; then
    fail "$cls: 실행된 테스트가 0개다"
  fi
  if [ "$skipped" -ne 0 ]; then
    fail "$cls: SKIPPED된 테스트가 $skipped 개 있다 (Docker를 쓸 수 없고 REQUIRE_DOCKER=true가 적용되지 않았을 가능성)"
  fi
  if [ "${failures:-0}" -ne 0 ] || [ "${errors:-0}" -ne 0 ]; then
    fail "$cls: 실패/오류가 있다 (failures=${failures:-0}, errors=${errors:-0})"
  fi
done

echo "== 전체 리포트의 SKIPPED 검사 (${#all_reports[@]}개 파일) =="
for report in "${all_reports[@]}"; do
  skipped="$(suite_attr "$report" skipped)"
  if [ -n "$skipped" ] && [ "$skipped" -ne 0 ]; then
    fail "SKIPPED된 테스트가 있다: ${report##*/} (skipped=$skipped)"
  fi
done

if [ "$failed" -ne 0 ]; then
  echo "통합 테스트 검증 실패" >&2
  exit 1
fi
echo "통합 테스트 클래스가 모두 실행됐고 SKIPPED가 없다"
