package com.pharmquest.pharmquest.domain.medicine.data;
import com.pharmquest.pharmquest.domain.medicine.data.enums.MedicineCategory;
public class MedicineCategoryMapper {

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
}