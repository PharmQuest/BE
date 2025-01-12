package com.pharmquest.pharmquest.domain.supplements.domain;

import com.pharmquest.pharmquest.domain.supplements.domain.Enums.Nation;
import jakarta.persistence.*;
import lombok.*;

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
    private String brand;

    @Column(nullable = false)
    private Nation nation;

    @Column(nullable = false, length = 255)
    private String image;

    @Column(nullable = false)
    private Boolean isScrapped;

    @Column(nullable = true, length = 100)
    private String category1;
    @Column(nullable = true, length = 100)
    private String category2;
    @Column(nullable = true, length = 100)
    private String category3;
    @Column(nullable = true, length = 100)
    private String category4;
}