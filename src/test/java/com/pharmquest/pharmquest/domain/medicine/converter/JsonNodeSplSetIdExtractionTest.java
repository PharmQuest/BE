package com.pharmquest.pharmquest.domain.medicine.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmquest.pharmquest.domain.medicine.repository.MedRepository;
import com.pharmquest.pharmquest.domain.medicine.repository.MedicineRepository;
import com.pharmquest.pharmquest.domain.medicine.repository.MedicineScrapRepository;
import com.pharmquest.pharmquest.domain.medicine.service.TranslationService;
import com.pharmquest.pharmquest.domain.user.repository.UserRepository;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

/**
 * P-001 문제 A 분리 검증: Jackson JsonNode의 실제 동작 특성화(characterization) 테스트.
 * 서비스 L274의 result.at("/openfda/spl_set_id").asText("Unknown") 패턴이 배열 응답에서 무엇을 반환하는지 확인한다.
 * 이 테스트들은 "현재 동작"을 고정하는 것이며, 기대 동작 판정은 MedicineServiceImplSaveRetryTest가 담당한다.
 */
class JsonNodeSplSetIdExtractionTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    @DisplayName("배열 spl_set_id에 asText(\"Unknown\")를 쓰면 첫 요소가 아니라 빈 문자열이 나온다")
    void arrayNode_asTextWithDefault_returnsEmptyString() throws Exception {
        JsonNode result = MAPPER.readTree("{\"openfda\":{\"spl_set_id\":[\"abc-123\"]}}");

        assertThat(result.at("/openfda/spl_set_id").asText("Unknown")).isEqualTo("");
        // 올바른 추출 방식 비교용
        assertThat(result.at("/openfda/spl_set_id").path(0).asText()).isEqualTo("abc-123");
    }

    @Test
    @DisplayName("스칼라 문자열이면 정상 추출된다 (문제가 배열 응답에 한정됨을 보여줌)")
    void scalarNode_asText_returnsValue() throws Exception {
        JsonNode result = MAPPER.readTree("{\"openfda\":{\"spl_set_id\":\"abc-123\"}}");

        assertThat(result.at("/openfda/spl_set_id").asText("Unknown")).isEqualTo("abc-123");
    }

    @Test
    @DisplayName("필드가 없으면 \"Unknown\"이 나온다")
    void missingNode_asTextWithDefault_returnsUnknown() throws Exception {
        JsonNode result = MAPPER.readTree("{\"openfda\":{}}");

        assertThat(result.at("/openfda/spl_set_id").asText("Unknown")).isEqualTo("Unknown");
    }

    @Test
    @DisplayName("[가설 확인] 서로 다른 배열 spl_set_id 두 건이 같은 키(\"\")가 된다 -> 이 값을 Set 키로 쓰면 충돌")
    void differentIds_collapseToSameKey() throws Exception {
        JsonNode a = MAPPER.readTree("{\"openfda\":{\"spl_set_id\":[\"A\"]}}");
        JsonNode b = MAPPER.readTree("{\"openfda\":{\"spl_set_id\":[\"B\"]}}");

        assertThat(a.at("/openfda/spl_set_id").asText("Unknown"))
                .isEqualTo(b.at("/openfda/spl_set_id").asText("Unknown"));
    }

    @Test
    @DisplayName("[가설 확인] 빈 배열 필드에서 MedicineConverter.hasRequiredRawFields가 NPE를 던진다 (현재 동작 고정)")
    void emptyArrayField_hasRequiredRawFields_throwsNpe() throws Exception {
        MedicineConverter converter = new MedicineConverter(
                mock(TranslationService.class), mock(MedicineRepository.class),
                mock(MedicineScrapRepository.class), mock(UserRepository.class),
                mock(MedRepository.class), new SimpleMeterRegistry());
        JsonNode result = MAPPER.readTree("{\"openfda\":{\"brand_name\":[]}}");

        assertThatThrownBy(() -> converter.hasRequiredRawFields(result))
                .isInstanceOf(NullPointerException.class);
    }
}
