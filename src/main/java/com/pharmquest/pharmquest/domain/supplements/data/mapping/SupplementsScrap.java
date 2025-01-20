package com.pharmquest.pharmquest.domain.supplements.data.mapping;

import com.pharmquest.pharmquest.domain.supplements.data.Supplements;
import com.pharmquest.pharmquest.global.data.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class SupplementsScrap extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @ManyToOne
//    @JoinColumn(name = "user_id", nullable = false)
//    private User user;

    @ManyToOne
    @JoinColumn(name = "sup_id", nullable = false)
    private Supplements supplements;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Boolean isSuccess;
}