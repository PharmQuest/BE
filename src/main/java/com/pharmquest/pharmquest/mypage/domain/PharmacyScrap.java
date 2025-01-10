package com.pharmquest.pharmquest.mypage.domain;

import com.pharmquest.pharmquest.pharmacy.domain.Pharmacy;
import com.pharmquest.pharmquest.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class PharmacyScrap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "pharmacy_id", referencedColumnName = "id", nullable = false)
    private Pharmacy pharmacy;
}
