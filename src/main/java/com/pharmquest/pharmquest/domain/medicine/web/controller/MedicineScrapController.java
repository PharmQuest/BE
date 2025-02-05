package com.pharmquest.pharmquest.domain.medicine.web.controller;

import com.pharmquest.pharmquest.domain.medicine.data.Medicine;
import com.pharmquest.pharmquest.domain.medicine.service.MedicineScrapService;
import com.pharmquest.pharmquest.domain.token.JwtUtil;
import com.pharmquest.pharmquest.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medicine/scrap")
@RequiredArgsConstructor
public class MedicineScrapController {

    private final MedicineScrapService scrapService;
    private final JwtUtil jwtUtil;

    /* 스크랩 추가 */
    @PostMapping("/add")
    public ApiResponse<String> addScrap
            (@Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam Long medicineId) {

        Long userId = jwtUtil.getUserFromHeader(authorizationHeader).getId();
        scrapService.addScrap(userId, medicineId);
        return ApiResponse.onSuccess("스크랩 성공!");
    }

    /* 스크랩 삭제 */
    @DeleteMapping("/remove")
    public ApiResponse<String> removeScrap
            (@Parameter(hidden = true) @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam Long medicineId) {

        Long userId = jwtUtil.getUserFromHeader(authorizationHeader).getId();
        scrapService.removeScrap(userId, medicineId);
        return ApiResponse.onSuccess("스크랩 삭제 완료!");
    }
}
