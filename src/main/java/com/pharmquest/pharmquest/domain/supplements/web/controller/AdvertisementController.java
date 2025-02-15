//package com.pharmquest.pharmquest.domain.supplements.web.controller;
//
//import com.pharmquest.pharmquest.domain.supplements.service.Advertisement.AdvertisementService;
//import com.pharmquest.pharmquest.domain.supplements.web.dto.AdResponseDTO;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/api/advertisements")
//@RequiredArgsConstructor
//public class AdvertisementController {
//    private final AdvertisementService advertisementService;
//
//    @GetMapping("/{id}")
//    public ResponseEntity<AdResponseDTO.AdLargeResponseDto> getAdvertisement(@PathVariable long id) {
//        return ResponseEntity.ok(advertisementService.getAdvertisementById(id));
//    }
//}