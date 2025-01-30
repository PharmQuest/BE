package com.pharmquest.pharmquest.domain.medicine.web.controller;

import com.pharmquest.pharmquest.domain.medicine.data.Medicine;
import com.pharmquest.pharmquest.domain.medicine.service.MedicineScrapService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicine/scrap")
public class MedicineScrapController {

    private final MedicineScrapService scrapService;

    public MedicineScrapController(MedicineScrapService scrapService) {
        this.scrapService = scrapService;
    }

    /** ✅ 1. 스크랩 추가 */
    @PostMapping("/add")
    public ResponseEntity<String> addScrap(@RequestParam Long userId, @RequestParam Long medicineId) {
        scrapService.addScrap(userId, medicineId);
        return ResponseEntity.ok("스크랩 성공!");
    }

    /** ✅ 2. 사용자의 스크랩 목록 조회 */
    @GetMapping("/list")
    public ResponseEntity<List<Medicine>> getScrappedMedicines(@RequestParam Long userId) {
        List<Medicine> scrappedMedicines = scrapService.getScrappedMedicines(userId);
        return ResponseEntity.ok(scrappedMedicines);
    }

    /** ✅ 3. 스크랩 삭제 */
    @DeleteMapping("/remove")
    public ResponseEntity<String> removeScrap(@RequestParam Long userId, @RequestParam Long medicineId) {
        scrapService.removeScrap(userId, medicineId);
        return ResponseEntity.ok("스크랩 삭제 완료!");
    }
}
