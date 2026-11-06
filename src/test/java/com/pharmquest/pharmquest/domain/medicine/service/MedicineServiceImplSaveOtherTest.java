package com.pharmquest.pharmquest.domain.medicine.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pharmquest.pharmquest.domain.medicine.converter.MedicineConverter;
import com.pharmquest.pharmquest.domain.medicine.data.Medicine;
import com.pharmquest.pharmquest.domain.medicine.data.enums.MedicineCategory;
import com.pharmquest.pharmquest.domain.medicine.repository.MedRepository;
import com.pharmquest.pharmquest.domain.medicine.repository.MedicineRepository;
import com.pharmquest.pharmquest.domain.medicine.repository.MedicineScrapRepository;
import com.pharmquest.pharmquest.domain.medicine.web.dto.MedicineSaveDetailResponseDTO;
import com.pharmquest.pharmquest.domain.user.repository.UserRepository;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * P-002: saveOtherMedicines 재현 테스트.
 * 외부 API와 DB는 Mockito로 대체하고 Spring 컨텍스트를 띄우지 않는다.
 * 단위 테스트이므로 @Transactional은 동작하지 않는다 (롤백/flush 동작은 검증 범위 밖).
 *
 * 컨버터 Mock은 실제 getFirstValue 규칙을 흉내 낸다: 배열이면 첫 요소, 없으면 "Unknown".
 * (실제 컨버터는 빈 배열에서 NPE가 나지만 여기서는 "Unknown"으로 대체한다.)
 */
class MedicineServiceImplSaveOtherTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String QUERY = "q";

    private MedicineRepository medicineRepository;
    private MedicineConverter medicineConverter;
    private MedRepository medRepository;
    private MedicineServiceImpl service;
    private List<String> convertedIds;

    @BeforeEach
    void setUp() {
        medicineRepository = mock(MedicineRepository.class);
        medicineConverter = mock(MedicineConverter.class);
        medRepository = mock(MedRepository.class);
        convertedIds = new ArrayList<>();

        when(medRepository.existsBySplSetId(anyString())).thenReturn(false);
        when(medRepository.save(any(Medicine.class))).thenAnswer(inv -> inv.getArgument(0));
        when(medicineConverter.SaveConvertToDetail(any(JsonNode.class), any())).thenAnswer(inv -> {
            JsonNode node = inv.getArgument(0);
            JsonNode idNode = node.at("/openfda/spl_set_id");
            String id = idNode.isArray() ? idNode.path(0).asText("Unknown") : idNode.asText("Unknown");
            convertedIds.add(id);
            return validDto(id);
        });

        service = new MedicineServiceImpl(
                medicineRepository, medicineConverter, medRepository,
                mock(MedicineScrapRepository.class), mock(UserRepository.class),
                new SimpleMeterRegistry(), mock(TransactionTemplate.class));
    }

    // T1 + 요구 2: 배열 형태 ID가 실제 ID로 추출되어 DB 중복 검사에 전달된다
    @Test
    @DisplayName("[T1] 배열 splSetId는 실제 ID(A,B,C)로 existsBySplSetId에 전달되어야 한다")
    void arrayId_isPassedToExistsCheck() throws Exception {
        when(medicineRepository.fetchMedicineData(anyString(), anyInt())).thenReturn(response("A", "B", "C"));

        service.saveOtherMedicines(QUERY, 3);

        verify(medRepository).existsBySplSetId("A");
        verify(medRepository).existsBySplSetId("B");
        verify(medRepository).existsBySplSetId("C");
        verify(medRepository, never()).existsBySplSetId("");
    }

    // T2: DB에 이미 있는 ID는 변환/저장하지 않는다
    @Test
    @DisplayName("[T2] DB에 이미 있는 ID(A)는 변환·저장하지 않고 나머지만 저장한다")
    void existingIdInDb_isSkipped() throws Exception {
        when(medRepository.existsBySplSetId("A")).thenReturn(true);
        when(medicineRepository.fetchMedicineData(anyString(), anyInt())).thenReturn(response("A", "B", "C"));

        List<Medicine> result = service.saveOtherMedicines(QUERY, 2);

        assertThat(ids(result)).containsExactly("B", "C");
        assertThat(convertedIds).doesNotContain("A");
        assertThat(savedIds()).doesNotContain("A");
    }

    // T3a: DB가 같은 요청 안의 저장분을 보지 못하는 최악 조건(Mock: 항상 false)에서 반복 응답
    @Test
    @DisplayName("[T3a] (DB가 항상 false인 조건) 재시도로 같은 ID가 다시 와도 중복 저장하지 않아야 한다")
    void repeatedIdAcrossRetries_statelessDb_noDuplicateSave() throws Exception {
        when(medicineRepository.fetchMedicineData(anyString(), anyInt()))
                .thenReturn(response("A", "B"))
                .thenReturn(response("A", "B", "C"));

        List<Medicine> result = service.saveOtherMedicines(QUERY, 3);

        assertThat(savedIds()).doesNotHaveDuplicates();
        assertThat(ids(result)).containsExactlyInAnyOrder("A", "B", "C");
    }

    // T3b: [가설 모사] 저장 직후 같은 트랜잭션의 existsBy가 그 행을 본다고 가정 (IDENTITY + auto flush).
    // 이 가정은 실제 Hibernate 동작으로 검증하지 않았다. 통과 여부는 "가정이 맞을 때"에 한한 결과다.
    @Test
    @DisplayName("[T3b][가설모사] 저장분을 DB가 즉시 본다고 가정하면 실제 ID 검사만으로 중복이 막혀야 한다")
    void repeatedIdAcrossRetries_statefulDb_noDuplicateSave() throws Exception {
        Set<String> persisted = new HashSet<>();
        when(medRepository.existsBySplSetId(anyString())).thenAnswer(inv -> persisted.contains(inv.<String>getArgument(0)));
        when(medRepository.save(any(Medicine.class))).thenAnswer(inv -> {
            Medicine m = inv.getArgument(0);
            persisted.add(m.getSplSetId());
            return m;
        });
        when(medicineRepository.fetchMedicineData(anyString(), anyInt()))
                .thenReturn(response("A", "B"))
                .thenReturn(response("A", "B", "C"));

        List<Medicine> result = service.saveOtherMedicines(QUERY, 3);

        assertThat(savedIds()).doesNotHaveDuplicates();
        assertThat(ids(result)).containsExactlyInAnyOrder("A", "B", "C");
    }

    // T3c: 한 응답 안의 중복
    @Test
    @DisplayName("[T3c] (DB가 항상 false인 조건) 한 응답 안의 같은 ID는 한 번만 저장해야 한다")
    void duplicateWithinResponse_statelessDb_savedOnce() throws Exception {
        when(medicineRepository.fetchMedicineData(anyString(), anyInt())).thenReturn(response("A", "A", "B"));

        List<Medicine> result = service.saveOtherMedicines(QUERY, 2);

        assertThat(ids(result)).containsExactlyInAnyOrder("A", "B");
    }

    // T4 + 요구 3: 무효 ID 항목은 저장되지 않고 요청은 계속된다
    @ParameterizedTest(name = "[T4] 무효 항목 {0} 은 저장되지 않는다")
    @ValueSource(strings = {
            "{\"openfda\":{}}",
            "{\"openfda\":{\"spl_set_id\":[]}}",
            "{\"openfda\":{\"spl_set_id\":[\"Unknown\"]}}",
            "{\"openfda\":{\"spl_set_id\":[\"  \"]}}",
            "{\"openfda\":{\"spl_set_id\":[\"\"]}}",
            "{\"openfda\":{\"spl_set_id\":[null]}}"
    })
    void invalidId_isNotSaved(String invalidItem) throws Exception {
        String body = "{\"results\":[" + invalidItem + "," + item("VALID") + "]}";
        when(medicineRepository.fetchMedicineData(anyString(), anyInt())).thenReturn(body);

        List<Medicine> result = service.saveOtherMedicines(QUERY, 1);

        assertThat(ids(result)).containsExactly("VALID");
    }

    // T5 + 요구 5: 정상 데이터 회귀
    @Test
    @DisplayName("[T5] 정상 응답은 1회 호출로 limit만큼 저장되고 필드와 카테고리가 유지된다")
    void normalData_savedAsBefore() throws Exception {
        when(medicineRepository.fetchMedicineData(anyString(), anyInt())).thenReturn(response("A", "B", "C"));

        List<Medicine> result = service.saveOtherMedicines(QUERY, 2);

        assertThat(ids(result)).containsExactly("A", "B");
        assertThat(result).allSatisfy(m -> {
            assertThat(m.getCategory()).isEqualTo(MedicineCategory.OTHER);
            assertThat(m.getCountry()).isEqualTo("USA");
            assertThat(m.getBrandName()).isEqualTo("brand-" + m.getSplSetId());
        });
        verify(medicineRepository, times(1)).fetchMedicineData(anyString(), anyInt());
        verify(medRepository, times(2)).save(any(Medicine.class));
    }

    // T6a + 요구 6: 서로 다른 유효 데이터가 limit에 못 미치면 예외
    @Test
    @DisplayName("[T6a] 서로 다른 유효 약이 limit(3) 미만(2)이면 예외가 발생한다")
    void insufficientDistinctData_throws() throws Exception {
        when(medicineRepository.fetchMedicineData(anyString(), anyInt())).thenReturn(response("A", "B"));

        assertThatThrownBy(() -> service.saveOtherMedicines(QUERY, 3)).isInstanceOf(RuntimeException.class);
    }

    // T6b: 변환 결과가 모두 무효이면 (이미지 실패 등) 기존 계약대로 3회 재시도 후 예외 [베이스라인]
    @Test
    @DisplayName("[T6b][베이스라인] 변환 결과가 모두 무효이면 3회 시도 후 저장 없이 예외")
    void allConversionsInvalid_throwsAfterThreeAttempts() throws Exception {
        when(medicineConverter.SaveConvertToDetail(any(JsonNode.class), any())).thenAnswer(inv -> {
            MedicineSaveDetailResponseDTO dto = validDto("A");
            dto.setImgUrl(null);
            return dto;
        });
        when(medicineRepository.fetchMedicineData(anyString(), anyInt())).thenReturn(response("A", "B"));

        assertThatThrownBy(() -> service.saveOtherMedicines(QUERY, 1)).isInstanceOf(RuntimeException.class);

        verify(medicineRepository, times(3)).fetchMedicineData(anyString(), anyInt());
        verify(medRepository, never()).save(any(Medicine.class));
    }

    // ---- helpers ----

    private List<String> savedIds() {
        ArgumentCaptor<Medicine> captor = ArgumentCaptor.forClass(Medicine.class);
        verify(medRepository, org.mockito.Mockito.atLeast(0)).save(captor.capture());
        return captor.getAllValues().stream().map(Medicine::getSplSetId).collect(Collectors.toList());
    }

    private static List<String> ids(List<Medicine> medicines) {
        return medicines.stream().map(Medicine::getSplSetId).collect(Collectors.toList());
    }

    private static String item(String id) {
        return "{\"openfda\":{\"spl_set_id\":[\"" + id + "\"]}}";
    }

    private static String response(String... ids) throws Exception {
        StringBuilder sb = new StringBuilder("{\"results\":[");
        for (int i = 0; i < ids.length; i++) {
            if (i > 0) sb.append(',');
            sb.append(item(ids[i]));
        }
        sb.append("]}");
        MAPPER.readTree(sb.toString());
        return sb.toString();
    }

    private static MedicineSaveDetailResponseDTO validDto(String splSetId) {
        return new MedicineSaveDetailResponseDTO(
                "brand-" + splSetId, "generic", "substance", "active", "purpose",
                "indications", "dosage", splSetId, "http://img/" + splSetId,
                "기타", "USA", "warnings");
    }
}
