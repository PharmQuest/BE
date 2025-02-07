package com.pharmquest.pharmquest.domain.medicine.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.pharmquest.pharmquest.domain.medicine.data.enums.MedicineCategory;
@Entity
@Getter
@Setter
@NoArgsConstructor
public class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String brandName;
    private String genericName;
    private String substanceName;
    private String activeIngredient;
    private String purpose;
    @Column(columnDefinition = "TEXT")
    private String indicationsAndUsage;
    @Column(columnDefinition = "TEXT")
    private String dosageAndAdministration;
    private String splSetId;
    private String imgUrl;
    @Enumerated(EnumType.STRING)
    private MedicineCategory category;
    private String country;
    @Column(columnDefinition = "TEXT")
    private String warnings;
}