package com.pharmquest.pharmquest.user.domain;

import com.pharmquest.pharmquest.mypage.domain.MedicineScrap;
import com.pharmquest.pharmquest.mypage.domain.PharmacyScrap;
import com.pharmquest.pharmquest.mypage.domain.SupplementScrap;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    // Relationships
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicineScrap> medicineScraps;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PharmacyScrap> pharmacyScraps;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SupplementScrap> supplementScraps;
}
