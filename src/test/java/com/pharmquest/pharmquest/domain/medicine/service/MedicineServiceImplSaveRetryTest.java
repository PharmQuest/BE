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
import org.mockito.ArgumentCaptor;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * P-001: saveMedicinesByCategory 재시도 루프의 중복 처리 재현 테스트.
 * 외부 API(openFDA/번역/이미지)와 DB는 전부 Mockito로 대체한다. Spring 컨텍스트를 띄우지 않는다.
 */
class MedicineServiceImplSaveRetryTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private MedicineRepository medicineRepository;
    private MedicineConverter medicineConverter;
    private MedRepository medRepository;
    private MedicineServiceImpl service;

    @BeforeEach
    void setUp() {
        medicineRepository = mock(MedicineRepository.class);
        medicineConverter = mock(MedicineConverter.class);
        medRepository = mock(MedRepository.class);
        TransactionTemplate transactionTemplate = mock(TransactionTemplate.class);

        // 기존 DB에는 어떤 splSetId도 없다
        when(medRepository.existsBySplSetId(anyString())).thenReturn(false);
        // 원본 필수값 검사는 모두 통과
        when(medicineConverter.hasRequiredRawFields(any(JsonNode.class))).thenReturn(true);
        // 변환/번역/이미지는 splSetId만 반영한 유효 DTO를 반환
        when(medicineConverter.SaveConvertToDetail(any(JsonNode.class), any())).thenAnswer(inv -> {
            JsonNode node = inv.getArgument(0);
            String id = node.at("/openfda/spl_set_id").path(0).asText(); // 실제 FDA 응답처럼 배열
            return validDto(id);
        });
        // 트랜잭션 콜백을 그대로 실행 (실제 DB 연결 없음)
        doAnswer(inv -> {
            TransactionCallback<?> cb = inv.getArgument(0);
            return cb.doInTransaction(null);
        }).when(transactionTemplate).execute(any());
        // saveAll은 받은 목록을 그대로 반환
        when(medRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        service = new MedicineServiceImpl(
                medicineRepository, medicineConverter, medRepository,
                mock(MedicineScrapRepository.class), mock(UserRepository.class),
                new SimpleMeterRegistry(), transactionTemplate);
    }

    @Test
    @DisplayName("[기대동작] 1회차 미달 후 2회차에 같은 splSetId가 다시 와도 저장 목록에 중복이 없어야 한다")
    void retry_doesNotAddDuplicateSplSetId() throws Exception {
        // limit=3. 1회차: A,B (유효 2건 < 3). 2회차: A,B,C
        when(medicineRepository.fetchMedicineData(anyString(), anyInt()))
                .thenReturn(response("A", "B"))
                .thenReturn(response("A", "B", "C"));

        service.saveMedicinesByCategory(MedicineCategory.PAIN_RELIEF, 3);

        List<String> saved = capturedSavedIds();
        assertThat(saved).doesNotHaveDuplicates();
        assertThat(saved).containsExactlyInAnyOrder("A", "B", "C");
    }

    @Test
    @DisplayName("[기대동작] 이미 후보에 오른 splSetId는 2회차에 다시 변환(번역/이미지 호출)하지 않아야 한다")
    void retry_doesNotReconvertSameSplSetId() throws Exception {
        when(medicineRepository.fetchMedicineData(anyString(), anyInt()))
                .thenReturn(response("A", "B"))
                .thenReturn(response("A", "B", "C"));

        service.saveMedicinesByCategory(MedicineCategory.PAIN_RELIEF, 3);

        // 호출 횟수 총합(3)만 보면 현재 코드(A,B,A)도 우연히 3이 되므로, splSetId별 변환 횟수를 검증한다.
        ArgumentCaptor<JsonNode> captor = ArgumentCaptor.forClass(JsonNode.class);
        verify(medicineConverter, org.mockito.Mockito.atLeast(0)).SaveConvertToDetail(captor.capture(), any());
        List<String> converted = captor.getAllValues().stream()
                .map(n -> n.at("/openfda/spl_set_id").path(0).asText())
                .collect(Collectors.toList());
        assertThat(converted).containsExactlyInAnyOrder("A", "B", "C");
    }

    @Test
    @DisplayName("[회귀/베이스라인] 1회차에 limit을 채우면 API는 1번만 호출되고 중복 없이 저장된다")
    void firstPassSatisfiesLimit_singleCall() throws Exception {
        when(medicineRepository.fetchMedicineData(anyString(), anyInt()))
                .thenReturn(response("A", "B", "C"));

        service.saveMedicinesByCategory(MedicineCategory.PAIN_RELIEF, 3);

        verify(medicineRepository, times(1)).fetchMedicineData(anyString(), anyInt());
        assertThat(capturedSavedIds()).containsExactly("A", "B", "C");
    }

    @Test
    @DisplayName("[회귀/베이스라인] 끝까지 limit 미달이면 예외가 발생하고 saveAll은 호출되지 않는다")
    void neverEnoughData_throwsAndDoesNotSave() throws Exception {
        when(medicineRepository.fetchMedicineData(anyString(), anyInt()))
                .thenReturn(response("A", "B"));

        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
                () -> service.saveMedicinesByCategory(MedicineCategory.PAIN_RELIEF, 3));

        verify(medRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("[진단] 중복 판단 키: DB 존재 확인에 실제 splSetId(\"A\")가 전달되어야 한다 (FDA는 spl_set_id를 배열로 반환)")
    void existsCheckReceivesActualSplSetId() throws Exception {
        when(medicineRepository.fetchMedicineData(anyString(), anyInt()))
                .thenReturn(response("A", "B", "C"));

        service.saveMedicinesByCategory(MedicineCategory.PAIN_RELIEF, 3);

        verify(medRepository).existsBySplSetId("A");
    }

    @Test
    @DisplayName("[변경A] 유효한 splSetId가 없는 항목(빈 배열/누락/Unknown)은 DB 검사·변환 없이 건너뛰고 요청은 계속된다")
    void itemsWithoutValidSplSetId_areSkippedWithoutFailingRequest() throws Exception {
        String body = "{\"results\":["
                + "{\"openfda\":{\"spl_set_id\":[]}},"
                + "{\"openfda\":{}},"
                + "{\"openfda\":{\"spl_set_id\":[\"Unknown\"]}},"
                + "{\"openfda\":{\"spl_set_id\":[\"A\"]}}"
                + "]}";
        when(medicineRepository.fetchMedicineData(anyString(), anyInt())).thenReturn(body);

        service.saveMedicinesByCategory(MedicineCategory.PAIN_RELIEF, 1);

        assertThat(capturedSavedIds()).containsExactly("A");
        verify(medRepository, never()).existsBySplSetId("");
        verify(medRepository, never()).existsBySplSetId("Unknown");
        verify(medicineConverter, times(1)).SaveConvertToDetail(any(JsonNode.class), any());
    }

    @Test
    @DisplayName("[변경A] 이미 DB에 있는 실제 splSetId는 변환 없이 제외되고, 나머지 정상 데이터는 제외되지 않는다")
    void existingSplSetIdInDb_isExcluded_othersKept() throws Exception {
        when(medRepository.existsBySplSetId("A")).thenReturn(true);
        when(medicineRepository.fetchMedicineData(anyString(), anyInt()))
                .thenReturn(response("A", "B", "C"));

        service.saveMedicinesByCategory(MedicineCategory.PAIN_RELIEF, 2);

        assertThat(capturedSavedIds()).containsExactly("B", "C");
        verify(medicineConverter, never()).SaveConvertToDetail(
                org.mockito.ArgumentMatchers.argThat(n -> "A".equals(n.at("/openfda/spl_set_id").path(0).asText())),
                any());
    }

    @Test
    @DisplayName("[변경B] 같은 응답 안에 같은 splSetId가 두 번 있어도 후보는 한 번만 담고, limit은 서로 다른 후보 수로 판단한다")
    void duplicateWithinSameResponse_countedOnce() throws Exception {
        when(medicineRepository.fetchMedicineData(anyString(), anyInt()))
                .thenReturn(response("A", "A", "B"));

        service.saveMedicinesByCategory(MedicineCategory.PAIN_RELIEF, 2);

        verify(medicineRepository, times(1)).fetchMedicineData(anyString(), anyInt());
        assertThat(capturedSavedIds()).containsExactly("A", "B");
    }

    @Test
    @DisplayName("[변경B] 변환 후 검증에 실패한(일시 실패) 항목은 Set에 기록되지 않아 다음 회차에 재시도되고, 성공한 항목은 재변환되지 않는다")
    void transientFailure_isRetried_whileSucceededItemsAreNot() throws Exception {
        java.util.concurrent.atomic.AtomicInteger cCalls = new java.util.concurrent.atomic.AtomicInteger();
        List<String> convertedIds = new java.util.ArrayList<>();
        when(medicineConverter.SaveConvertToDetail(any(JsonNode.class), any())).thenAnswer(inv -> {
            JsonNode node = inv.getArgument(0);
            String id = node.at("/openfda/spl_set_id").path(0).asText();
            convertedIds.add(id);
            MedicineSaveDetailResponseDTO dto = validDto(id);
            if (id.equals("C") && cCalls.incrementAndGet() == 1) {
                dto.setImgUrl(null); // 1회차: 이미지 조회 일시 실패 -> isValid 탈락
            }
            return dto;
        });
        when(medicineRepository.fetchMedicineData(anyString(), anyInt()))
                .thenReturn(response("A", "B", "C"))
                .thenReturn(response("A", "B", "C"));

        service.saveMedicinesByCategory(MedicineCategory.PAIN_RELIEF, 3);

        assertThat(capturedSavedIds()).containsExactly("A", "B", "C");
        assertThat(convertedIds).containsExactly("A", "B", "C", "C"); // A,B는 1회, C만 재시도
        verify(medicineRepository, times(2)).fetchMedicineData(anyString(), anyInt());
    }

    // ---- helpers ----

    private List<String> capturedSavedIds() {
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Medicine>> captor = ArgumentCaptor.forClass(List.class);
        verify(medRepository).saveAll(captor.capture());
        return captor.getValue().stream().map(Medicine::getSplSetId).collect(Collectors.toList());
    }

    private static String response(String... splSetIds) throws Exception {
        StringBuilder sb = new StringBuilder("{\"results\":[");
        for (int i = 0; i < splSetIds.length; i++) {
            if (i > 0) sb.append(',');
            sb.append("{\"openfda\":{\"spl_set_id\":[\"").append(splSetIds[i]).append("\"]}}");
        }
        sb.append("]}");
        MAPPER.readTree(sb.toString()); // JSON 유효성 확인
        return sb.toString();
    }

    private static MedicineSaveDetailResponseDTO validDto(String splSetId) {
        return new MedicineSaveDetailResponseDTO(
                "brand-" + splSetId, "generic", "substance", "active", "purpose",
                "indications", "dosage", splSetId, "http://img/" + splSetId,
                "진통/해열", "USA", "warnings");
    }
}
