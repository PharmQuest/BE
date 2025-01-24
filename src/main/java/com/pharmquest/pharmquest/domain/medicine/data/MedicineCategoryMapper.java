package com.pharmquest.pharmquest.domain.medicine.data;

public class MedicineCategoryMapper {

    public static String getCategory(String purpose, String activeIngredient, String pharmClassEpc, String route) {
        // Null-safe 초기화
        if (purpose == null) purpose = "";
        if (activeIngredient == null) activeIngredient = "";
        if (pharmClassEpc == null) pharmClassEpc = "";
        if (route == null) route = "";

        // 진통/해열
        if (purpose.contains("Pain reliever") || purpose.contains("Fever reducer") ||
                activeIngredient.contains("Acetaminophen") || activeIngredient.contains("Naproxen") || activeIngredient.contains("Ibuprofen") ||
                pharmClassEpc.contains("Analgesic") || pharmClassEpc.contains("Antipyretic")) {
            return "진통/해열";
        }

        // 소화/위장
        if (purpose.contains("Antacid") || purpose.contains("Acid reducer") ||
                pharmClassEpc.contains("Antacid") || pharmClassEpc.contains("Proton pump inhibitor")) {
            return "소화/위장";
        }

        // 감기/기침
        if (purpose.contains("Cough suppressant") || purpose.contains("Expectorant") ||
                pharmClassEpc.contains("Decongestant")) {
            return "감기/기침";
        }

        // 알레르기
        if (purpose.contains("Antihistamine") || pharmClassEpc.contains("Antihistamine")) {
            return "알레르기";
        }

        // 상처/소독
        if (purpose.contains("Antiseptic") || pharmClassEpc.contains("Antiseptic")) {
            return "상처/소독";
        }

        // 멀미
        if (purpose.contains("Antiemetic") || pharmClassEpc.contains("Antiemetic")) {
            return "멀미";
        }

        // 안약
        if (route.contains("OPHTHALMIC")) {
            return "안약";
        }

        // 기타
        return "기타";
    }
}