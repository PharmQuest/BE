package com.pharmquest.pharmquest.domain.supplements.data;

import com.pharmquest.pharmquest.domain.supplements.data.enums.Nation;
import com.pharmquest.pharmquest.domain.supplements.data.mapping.SupplementsScrap;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = true, length = 255)
    private String description;

    @Column(nullable = false, length = 100)
    private String maker;

    @Column(nullable = false, length = 100)
    private String brand;

    @Column(nullable = false, length = 255)
    private String link;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Nation nation;

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

    @OneToMany(mappedBy = "supplements", cascade = CascadeType.ALL)
    private List<SupplementsScrap> supplementsScrapList = new ArrayList<>();
}
