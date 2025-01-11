package com.pharmquest.pharmquest.domain.supplements.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Supplements {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
}
