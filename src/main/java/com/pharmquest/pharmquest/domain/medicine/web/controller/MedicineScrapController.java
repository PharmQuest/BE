package com.pharmquest.pharmquest.domain.medicine.web.controller;

import com.pharmquest.pharmquest.domain.medicine.data.Medicine;
import com.pharmquest.pharmquest.domain.medicine.service.MedicineScrapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicine/scrap")
@RequiredArgsConstructor
public class MedicineScrapController {

    private final MedicineScrapService scrapService;

    /* 스크랩 추가 */
    @PostMapping("/add")
    public ResponseEntity<String> addScrap(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam Long medicineId) {
        scrapService.addScrap(authorizationHeader, medicineId);
        return ResponseEntity.ok("스크랩 성공!");
    }

    /* 스크랩 목록 조회 */
    @GetMapping("/list")
    public ResponseEntity<List<Medicine>> getScrappedMedicines(@RequestHeader("Authorization") String authorizationHeader) {
        List<Medicine> scrappedMedicines = scrapService.getScrappedMedicines(authorizationHeader);
        return ResponseEntity.ok(scrappedMedicines);
    }

    /* 스크랩 삭제 */
    @DeleteMapping("/remove")
    public ResponseEntity<String> removeScrap(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam Long medicineId) {
        scrapService.removeScrap(authorizationHeader, medicineId);
        return ResponseEntity.ok("스크랩 삭제 완료!");
    }
}