package com.pharmquest.pharmquest.domain.pharmacy.data;

import com.pharmquest.pharmquest.domain.pharmacy.data.enums.PharmacyCountry;
import com.pharmquest.pharmquest.global.data.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Entity
@Getter
@Builder
@Slf4j
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Pharmacy extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false )
    private String placeId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String region;

    @Enumerated(EnumType.STRING)
    private PharmacyCountry country;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    @Setter
    private String imgUrl;

}

