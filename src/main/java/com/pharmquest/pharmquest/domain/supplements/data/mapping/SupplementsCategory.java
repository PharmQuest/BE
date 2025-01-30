package com.pharmquest.pharmquest.domain.supplements.data.mapping;

import com.pharmquest.pharmquest.domain.supplements.data.Category;
import com.pharmquest.pharmquest.domain.supplements.data.Supplements;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "supplement_category")

public class SupplementsCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "supplement_id")
    private Supplements supplement;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}