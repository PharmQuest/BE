package com.pharmquest.pharmquest.domain.user.data;

import com.pharmquest.pharmquest.domain.mypage.domain.MedicineScrap;
import com.pharmquest.pharmquest.domain.mypage.domain.PharmacyScrap;
import com.pharmquest.pharmquest.domain.mypage.domain.SupplementScrap;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID userId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private String provider;

    private String providerId;

    @CreationTimestamp
    private Timestamp createDate;

    // Relationships
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicineScrap> medicineScraps;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PharmacyScrap> pharmacyScraps;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SupplementScrap> supplementScraps;
}
