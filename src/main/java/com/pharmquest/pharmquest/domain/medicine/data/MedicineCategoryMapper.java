package com.pharmquest.pharmquest.domain.medicine.data;
import com.pharmquest.pharmquest.domain.medicine.data.enums.MedicineCategory;

import java.util.HashMap;
import java.util.Map;

public class MedicineCategoryMapper {

    private static final Map<MedicineCategory, String> categoryToKorean = new HashMap<>();
    private static final Map<String, MedicineCategory> koreanToCategory = new HashMap<>();
    private static final Map<MedicineCategory, String> categoryToEffectKeyword = new HashMap<>();
    static {
        categoryToKorean.put(MedicineCategory.PAIN_RELIEF, "진통/해열");
        categoryToKorean.put(MedicineCategory.DIGESTIVE, "소화/위장");
        categoryToKorean.put(MedicineCategory.COLD, "감기/기침");
        categoryToKorean.put(MedicineCategory.ALLERGY, "알레르기");
        categoryToKorean.put(MedicineCategory.ANTISEPTIC, "상처/소독");
        categoryToKorean.put(MedicineCategory.MOTION_SICKNESS, "멀미");
        categoryToKorean.put(MedicineCategory.EYE_DROPS, "안약");
        categoryToKorean.put(MedicineCategory.OTHER, "기타");
        categoryToKorean.put(MedicineCategory.ALL, "전체");

        // 역변환을 위한 매핑
        for (Map.Entry<MedicineCategory, String> entry : categoryToKorean.entrySet()) {
            koreanToCategory.put(entry.getValue(), entry.getKey());
        }

        categoryToEffectKeyword.put(MedicineCategory.PAIN_RELIEF, "진통 해열");
        categoryToEffectKeyword.put(MedicineCategory.DIGESTIVE, "소화 위장");
        categoryToEffectKeyword.put(MedicineCategory.COLD, "감기 기침 콧물");
        categoryToEffectKeyword.put(MedicineCategory.ALLERGY, "알레르기 두드러기");
        categoryToEffectKeyword.put(MedicineCategory.ANTISEPTIC, "상처 소독");
        categoryToEffectKeyword.put(MedicineCategory.MOTION_SICKNESS, "멀미");
        categoryToEffectKeyword.put(MedicineCategory.EYE_DROPS, "눈 안약");
    }

    // ✅ 영어 -> 한글 변환
    public static String toKoreanCategory(MedicineCategory category) {
        return categoryToKorean.getOrDefault(category, "기타");
    }

    // ✅ 한글 -> 영어 변환
    public static MedicineCategory toEnglishCategory(String koreanCategory) {
        return koreanToCategory.getOrDefault(koreanCategory, MedicineCategory.OTHER);
    }

    public static MedicineCategory getCategory(String purpose, String activeIngredient, String pharmClassEpc, String route) {
        // Null-safe 초기화
        if (purpose == null) purpose = "";
        if (activeIngredient == null) activeIngredient = "";
        if (pharmClassEpc == null) pharmClassEpc = "";
        if (route == null) route = "";

        // 진통/해열
        if (purpose.contains("Pain reliever") || purpose.contains("Fever reducer") ||
                activeIngredient.contains("Acetaminophen") || activeIngredient.contains("Naproxen") || activeIngredient.contains("Ibuprofen") ||
                pharmClassEpc.contains("Analgesic") || pharmClassEpc.contains("Antipyretic")) {
            return  MedicineCategory.PAIN_RELIEF;
        }

        // 소화/위장
        if (purpose.contains("Antacid") || purpose.contains("Acid reducer") ||
                pharmClassEpc.contains("Antacid") || pharmClassEpc.contains("Proton pump inhibitor")) {
            return MedicineCategory.DIGESTIVE;
        }

        // 감기/기침
        if (purpose.contains("Cough suppressant") || purpose.contains("Expectorant") ||
                pharmClassEpc.contains("Decongestant")) {
            return MedicineCategory.COLD;
        }

        // 알레르기
        if (purpose.contains("Antihistamine") || pharmClassEpc.contains("Antihistamine")) {
            return MedicineCategory.ALLERGY;
        }

        // 상처/소독
        if (purpose.contains("Antiseptic") || pharmClassEpc.contains("Antiseptic")) {
            return MedicineCategory.ANTISEPTIC;
        }

        // 멀미
        if (purpose.contains("Antiemetic") || pharmClassEpc.contains("Antiemetic")) {
            return MedicineCategory.MOTION_SICKNESS;
        }

        // 안약
        if (route.contains("OPHTHALMIC")) {
            return MedicineCategory.EYE_DROPS;
        }

        // 기타
        return MedicineCategory.OTHER;
    }

    public static String getQueryForCategory(MedicineCategory category) {
        switch (category) {
            case PAIN_RELIEF:
                return "purpose:(\"Pain reliever\" OR \"Fever reducer\") OR " +
                        "active_ingredient:(\"Acetaminophen\" OR \"Naproxen\" OR \"Ibuprofen\") OR " +
                        "pharm_class_epc:(\"Analgesic\" OR \"Antipyretic\")";
            case DIGESTIVE:
                return "purpose:(\"Antacid\" OR \"Acid reducer\") OR " +
                        "pharm_class_epc:(\"Antacid\" OR \"Proton pump inhibitor\")";
            case COLD:
                return "purpose:(\"Cough suppressant\" OR \"Expectorant\") OR " +
                        "pharm_class_epc:(\"Decongestant\")";
            case ALLERGY:
                return "purpose:(\"Antihistamine\") OR pharm_class_epc:(\"Antihistamine\")";
            case ANTISEPTIC:
                return "purpose:(\"Antiseptic\") OR pharm_class_epc:(\"Antiseptic\")";
            case MOTION_SICKNESS:
                return "purpose:(\"Antiemetic\") OR pharm_class_epc:(\"Antiemetic\")";
            case EYE_DROPS:
                return "openfda.route:(\"OPHTHALMIC\")";
            case OTHER:
                return "NOT (purpose:(\"Pain reliever\" OR \"Fever reducer\" OR \"Antacid\" OR " +
                        "\"Acid reducer\" OR \"Cough suppressant\" OR \"Expectorant\" OR \"Antihistamine\" OR " +
                        "\"Antiseptic\" OR \"Antiemetic\") OR " +
                        "active_ingredient:(\"Acetaminophen\" OR \"Naproxen\" OR \"Ibuprofen\") OR " +
                        "pharm_class_epc:(\"Analgesic\" OR \"Antipyretic\" OR \"Antacid\" OR \"Proton pump inhibitor\" OR " +
                        "\"Decongestant\" OR \"Antihistamine\" OR \"Antiseptic\" OR \"Antiemetic\") OR " +
                        "openfda.route:(\"OPHTHALMIC\"))";
            case ALL:
            default:
                return null; // 전체 조회 시 별도 쿼리 없음
        }
    }

    // ✅ `MedicineCategory`을 `efcyQesitm` 검색 키워드로 변환하는 메서드 추가
    public static String getEffectKeywordForCategory(MedicineCategory category) {
        return categoryToEffectKeyword.get(category);
    }

    // ✅ `efcyQesitm`을 기반으로 `MedicineCategory`를 판별하는 새 메서드 추가
    public static boolean isOtherCategory(String efcyQesitm) {
        if (efcyQesitm == null || efcyQesitm.isEmpty()) {
            return true;
        }

        for (String keywords : categoryToEffectKeyword.values()) {
            for (String keyword : keywords.split(" ")) {
                if (efcyQesitm.contains(keyword)) {
                    return false; // ✅ 다른 카테고리에 포함되면 OTHER이 아님
                }
            }
        }

        return true; // ✅ OTHER 카테고리에 해당
    }
}
