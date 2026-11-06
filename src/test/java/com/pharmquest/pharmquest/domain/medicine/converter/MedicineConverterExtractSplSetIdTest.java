package com.pharmquest.pharmquest.domain.medicine.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/** P-001 변경 A: MedicineConverter.extractSplSetId 경계 조건 테스트 (기대 동작 기준). */
class MedicineConverterExtractSplSetIdTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static String extract(String json) throws Exception {
        return MedicineConverter.extractSplSetId(MAPPER.readTree(json));
    }

    @Test
    @DisplayName("배열이면 첫 요소를 반환한다 (실제 openFDA 응답 형태)")
    void array_returnsFirstElement() throws Exception {
        assertThat(extract("{\"openfda\":{\"spl_set_id\":[\"abc-123\",\"def-456\"]}}")).isEqualTo("abc-123");
    }

    @Test
    @DisplayName("스칼라 문자열도 그대로 반환한다")
    void scalar_returnsValue() throws Exception {
        assertThat(extract("{\"openfda\":{\"spl_set_id\":\"abc-123\"}}")).isEqualTo("abc-123");
    }

    @ParameterizedTest(name = "유효하지 않은 입력 -> null : {0}")
    @ValueSource(strings = {
            "{}",                                          // openfda 자체가 없음
            "{\"openfda\":{}}",                            // 필드 누락
            "{\"openfda\":{\"spl_set_id\":[]}}",           // 빈 배열
            "{\"openfda\":{\"spl_set_id\":null}}",         // null
            "{\"openfda\":{\"spl_set_id\":[null]}}",       // 배열 안의 null
            "{\"openfda\":{\"spl_set_id\":\"\"}}",         // 빈 문자열
            "{\"openfda\":{\"spl_set_id\":[\"\"]}}",       // 배열 안의 빈 문자열
            "{\"openfda\":{\"spl_set_id\":\"   \"}}",      // 공백
            "{\"openfda\":{\"spl_set_id\":[\"Unknown\"]}}",// Unknown
            "{\"openfda\":{\"spl_set_id\":[\"unknown\"]}}",// Unknown (대소문자 무시)
            "{\"openfda\":{\"spl_set_id\":[{\"a\":1}]}}",  // 값이 아닌 객체
            "{\"openfda\":{\"spl_set_id\":{\"a\":1}}}"     // 객체
    })
    void invalidInputs_returnNull(String json) throws Exception {
        assertThat(extract(json)).isNull();
    }

    @Test
    @DisplayName("result 자체가 null이면 null")
    void nullResult_returnsNull() {
        assertThat(MedicineConverter.extractSplSetId(null)).isNull();
    }

    @Test
    @DisplayName("저장되는 DTO의 식별자 규칙(getFirstValue)과 같은 값을 돌려준다: 첫 요소, 가공 없음")
    void doesNotTrimOrTransform() throws Exception {
        JsonNode node = MAPPER.readTree("{\"openfda\":{\"spl_set_id\":[\" Ab-1 \"]}}");
        assertThat(MedicineConverter.extractSplSetId(node)).isEqualTo(" Ab-1 ");
    }
}
