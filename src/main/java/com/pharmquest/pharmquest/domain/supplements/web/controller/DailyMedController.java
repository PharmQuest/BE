//package com.pharmquest.pharmquest.domain.supplements.web.controller;
//
//import com.pharmquest.pharmquest.domain.supplements.service.DailyMed.DailyMedService;
//import com.pharmquest.pharmquest.domain.supplements.web.dto.DailyMedResponseDTO;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("/api/supplements")
//@RequiredArgsConstructor
//public class DailyMedController {
//    private final DailyMedService dailyMedService;
//
//    @GetMapping("/daily-med")
//    public ResponseEntity<List<DailyMedResponseDTO.DetailInfo>> getDailyMedList() {
//        List<DailyMedResponseDTO.ExtractedInfo> result = dailyMedService.extractSupplementInfo();
//        List<DailyMedResponseDTO.DetailInfo> detailInfoList = result.stream()
//                .map(info -> dailyMedService.getDetailInfo(info.getSetid(), info.getTitle()))
//                .collect(Collectors.toList());
//
//        return ResponseEntity.ok(detailInfoList);
//    }
//}