package com.pharmquest.pharmquest.domain.supplements.data;

import com.pharmquest.pharmquest.domain.post.data.enums.Country;
import com.pharmquest.pharmquest.domain.supplements.data.mapping.SupplementsCategory;
import com.pharmquest.pharmquest.domain.supplements.data.mapping.SupplementsScrap;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Supplements {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String name;

    @Column(nullable = false, length = 100)
    private String maker;

    @Column(nullable = false, length = 100)
    private String brand;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Country country;

    @Column(nullable = false, length = 255)
    private String image;

    @Column(nullable = false)
    private int scrapCount;

    @Column(nullable = true, length = 100)
    private String category1;
    @Column(nullable = true, length = 100)
    private String category2;
    @Column(nullable = true, length = 100)
    private String category3;
    @Column(nullable = true, length = 100)
    private String category4;
    @Column(nullable = true, columnDefinition = "TEXT")
    private String dosage;
    @Column(nullable = true, columnDefinition = "TEXT")
    private String warning;
    @Column(nullable = true, columnDefinition = "TEXT")
    private String purpose;

    @OneToMany(mappedBy = "supplements", cascade = CascadeType.ALL)
    private List<SupplementsScrap> supplementsScrapList = new ArrayList<>();

    @OneToMany(mappedBy = "supplement")
    private List<SupplementsCategory> supplementsCategoryList = new ArrayList<>();
}
