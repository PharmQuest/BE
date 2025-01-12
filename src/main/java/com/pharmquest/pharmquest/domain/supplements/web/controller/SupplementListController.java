package com.pharmquest.pharmquest.domain.supplements.web.controller;

import com.pharmquest.pharmquest.common.apiPayload.ApiResponse;

import com.pharmquest.pharmquest.domain.supplements.domain.Supplements;
import com.pharmquest.pharmquest.domain.supplements.service.SupplementsService;
import com.pharmquest.pharmquest.domain.supplements.web.dto.SupplementsResponseDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "해외 인기 영양제", description = "해외 인기 영양제")
@RestController
@RequestMapping("/api/v1/supplements")
@RequiredArgsConstructor
public class SupplementListController {

    private final SupplementsService supplementsService;

    @GetMapping("/lists")
    public ApiResponse<List<SupplementsResponseDTO.SupplementsDto>> getSupplements() {

//        supplementsService.saveSupplements();

        List<Supplements> supplements = supplementsService.getSupplements();

        List<SupplementsResponseDTO.SupplementsDto> supplementsDtoList = supplements.stream()
                .map(supplement -> SupplementsResponseDTO.SupplementsDto.builder()
                        .name(supplement.getName())
                        .image(supplement.getImage())
                        .brand(supplement.getBrand())
                        .build())
                .collect(Collectors.toList());

        return ApiResponse.onSuccess(supplementsDtoList);
    }
}