package com.pharmquest.pharmquest.domain.medicine.data.enums;

import io.swagger.v3.oas.annotations.media.Schema;

public enum MedicineCategory {

    @Schema(description = "진통/해열")
    PAIN_RELIEF,

    @Schema(description = "소화/위장")
    DIGESTIVE,

    @Schema(description = "감기/기침")
    COLD,

    @Schema(description = "알레르기")
    ALLERGY,

    @Schema(description = "상처/소독")
    ANTISEPTIC,

    @Schema(description = "멀미")
    MOTION_SICKNESS,

    @Schema(description = "안약")
    EYE_DROPS,

    @Schema(description = "기타")
    OTHER,

    @Schema(description = "전체")
    ALL;
}