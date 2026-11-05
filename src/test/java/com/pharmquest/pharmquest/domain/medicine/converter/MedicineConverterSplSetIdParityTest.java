package com.pharmquest.pharmquest.domain.medicine.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmquest.pharmquest.domain.medicine.repository.MedRepository;
import com.pharmquest.pharmquest.domain.medicine.repository.MedicineRepository;
import com.pharmquest.pharmquest.domain.medicine.repository.MedicineScrapRepository;
import com.pharmquest.pharmquest.domain.medicine.service.TranslationService;
import com.pharmquest.pharmquest.domain.medicine.web.dto.MedicineSaveDetailResponseDTO;
import com.pharmquest.pharmquest.domain.user.repository.UserRepository;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.Executor;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * B-006: 중복 검사 키(extractSplSetId)와 실제 저장 DTO의 식별자(SaveConvertToDetail의 splSetId)가
 * 실제 MedicineConverter 실행에서 일치하는지 확인한다.
 *
 * <p>컨버터는 실제 객체다. 외부 경계만 대체한다: 번역(입력 그대로 반환), 이미지 조회(null 반환 -> 컨버터가 내부에서 처리),
 * Executor(호출 스레드에서 동기 실행). 변환 결과를 테스트가 직접 만들어 주입하지 않는다.
 *
 * <p>무효 식별자는 현재 컨버터의 동작을 <b>기록</b>만 하고, 그 동작을 기대값으로 고정하지 않는다.
 */
class MedicineConverterSplSetIdParityTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private MedicineConverter converter;

    @BeforeEach
    void setUp() {
        TranslationService translationService = mock(TranslationService.class);
        when(translationService.translateText(anyString(), anyString()))
                .thenAnswer(inv -> inv.getArgument(0));

        converter = new MedicineConverter(
                translationService, mock(MedicineRepository.class),
                mock(MedicineScrapRepository.class), mock(UserRepository.class),
                mock(MedRepository.class), new SimpleMeterRegistry());

        Executor direct = Runnable::run;
        ReflectionTestUtils.setField(converter, "translationExecutor", direct);
        ReflectionTestUtils.setField(converter, "imageExecutor", direct);
    }

    /** spl_set_id 부분만 바꾸고 나머지 필수 필드는 유효한 배열 값으로 채운다. splJson이 null이면 필드를 넣지 않는다. */
    private static JsonNode fixture(String splJson) throws Exception {
        String spl = splJson == null ? "" : ",\"spl_set_id\":" + splJson;
        String json = "{"
                + "\"openfda\":{\"brand_name\":[\"Brand\"],\"generic_name\":[\"Generic\"],"
                + "\"substance_name\":[\"Substance\"],\"route\":[\"ORAL\"]" + spl + "},"
                + "\"active_ingredient\":[\"Ingredient\"],\"purpose\":[\"Pain\"],"
                + "\"indications_and_usage\":[\"Use\"],\"dosage_and_administration\":[\"Dose\"],"
                + "\"warnings\":[\"Warn\"]}";
        return MAPPER.readTree(json);
    }

    static Stream<String> validCases() {
        return Stream.of(
                "[\"abc\"]",        // 배열
                "\"abc\"",          // 문자열
                "[\" Ab-1 \"]",     // 앞뒤 공백 (가공 여부)
                "[\"A\",\"B\"]"     // 복수 요소 -> 첫 요소
        );
    }

    static Stream<String> invalidCases() {
        return Stream.of(
                null,               // 필드 누락
                "[]",               // 빈 배열
                "null",             // null
                "[null]",           // 배열 안의 null
                "\"Unknown\"",      // Unknown
                "\"   \"",          // 공백
                "{\"a\":1}",        // 객체
                "[\"\",\"x\"]"      // 첫 요소가 빈 값 (둘째가 유효해도 첫 요소만 본다)
        );
    }

    @ParameterizedTest(name = "[식별자 입력] {0}")
    @MethodSource("validCases")
    @DisplayName("정상 식별자: extractSplSetId는 값을 반환하고 실제 SaveConvertToDetail의 DTO 식별자와 같아야 한다")
    void validOrBoundary_extractedEqualsDtoId(String splJson) throws Exception {
        JsonNode node = fixture(splJson);

        String extracted = MedicineConverter.extractSplSetId(node);
        MedicineSaveDetailResponseDTO dto = converter.SaveConvertToDetail(node, null);

        System.out.println("[B-006] input=" + splJson + " extracted=" + quote(extracted)
                + " dto=" + quote(dto.getSplSetId()));

        assertThat(extracted).isNotNull();
        assertThat(dto.getSplSetId()).isEqualTo(extracted);
    }

    @Test
    @DisplayName("[기대 확인] 정상 식별자 abc는 추출값과 DTO 값이 모두 abc다 (오탐 방지용 구체 값)")
    void concreteValue_abc() throws Exception {
        JsonNode node = fixture("[\"abc\"]");

        assertThat(MedicineConverter.extractSplSetId(node)).isEqualTo("abc");
        assertThat(converter.SaveConvertToDetail(node, null).getSplSetId()).isEqualTo("abc");
    }

    @ParameterizedTest(name = "[무효 식별자 기록] {0}")
    @MethodSource("invalidCases")
    @DisplayName("무효 식별자: 서비스 사전 필터의 전제(extractSplSetId == null)만 확인하고, 실제 컨버터 결과는 기록한다")
    void invalid_recordConverterBehavior(String splJson) throws Exception {
        JsonNode node = fixture(splJson);

        // 서비스(P-001/P-002)는 이 값이 null이면 변환 이전에 걸러낸다.
        assertThat(MedicineConverter.extractSplSetId(node)).isNull();

        // 서비스가 걸러내므로 실제로 도달하지 않는 경로. 결과/예외를 출력만 하고 단언하지 않는다.
        String outcome;
        try {
            MedicineSaveDetailResponseDTO dto = converter.SaveConvertToDetail(node, null);
            outcome = "dto.splSetId=" + quote(dto.getSplSetId());
        } catch (Exception e) {
            outcome = "EXCEPTION " + e.getClass().getSimpleName() + ": " + e.getMessage();
        }
        System.out.println("[B-006] invalid input=" + splJson + " -> " + outcome);
    }

    private static String quote(String s) {
        return s == null ? "null" : "'" + s + "'";
    }
}
