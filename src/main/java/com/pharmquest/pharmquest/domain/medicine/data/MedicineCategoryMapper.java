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

    public static String getQueryForCategory(String category) {
        switch (category) {
            case "1":
                return "purpose:(\"Pain reliever\" OR \"Fever reducer\") OR " +
                        "active_ingredient:(\"Acetaminophen\" OR \"Naproxen\" OR \"Ibuprofen\") OR " +
                        "pharm_class_epc:(\"Analgesic\" OR \"Antipyretic\")";
            case "2":
                return "purpose:(\"Antacid\" OR \"Acid reducer\") OR " +
                        "pharm_class_epc:(\"Antacid\" OR \"Proton pump inhibitor\")";
            case "3":
                return "purpose:(\"Cough suppressant\" OR \"Expectorant\") OR " +
                        "pharm_class_epc:(\"Decongestant\")";
            case "4":
                return "purpose:(\"Antihistamine\") OR pharm_class_epc:(\"Antihistamine\")";
            case "5":
                return "purpose:(\"Antiseptic\") OR pharm_class_epc:(\"Antiseptic\")";
            case "6":
                return "purpose:(\"Antiemetic\") OR pharm_class_epc:(\"Antiemetic\")";
            case "7":
                return "openfda.route:(\"OPHTHALMIC\")";
            case "기타":
                // "기타"는 위의 모든 카테고리를 제외하는 쿼리
                return "NOT (purpose:(\"Pain reliever\" OR \"Fever reducer\" OR \"Antacid\" OR " +
                        "\"Acid reducer\" OR \"Cough suppressant\" OR \"Expectorant\" OR \"Antihistamine\" OR " +
                        "\"Antiseptic\" OR \"Antiemetic\") OR " +
                        "active_ingredient:(\"Acetaminophen\" OR \"Naproxen\" OR \"Ibuprofen\") OR " +
                        "pharm_class_epc:(\"Analgesic\" OR \"Antipyretic\" OR \"Antacid\" OR \"Proton pump inhibitor\" OR " +
                        "\"Decongestant\" OR \"Antihistamine\" OR \"Antiseptic\" OR \"Antiemetic\") OR " +
                        "openfda.route:(\"OPHTHALMIC\"))";
            default:
                return null; // 매칭되는 카테고리 없음
        }
    }
}