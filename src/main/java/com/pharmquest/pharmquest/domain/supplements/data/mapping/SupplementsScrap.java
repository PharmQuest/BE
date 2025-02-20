package com.pharmquest.pharmquest.domain.supplements.data.mapping;

import com.pharmquest.pharmquest.domain.supplements.data.Supplements;
import com.pharmquest.pharmquest.domain.user.data.User;
import com.pharmquest.pharmquest.global.data.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_user_supplement",
                columnNames = {"user_id", "sup_id"}
        )
})
public class SupplementsScrap extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "sup_id", nullable = false)
    private Supplements supplements;

    @Column(nullable = false)
    private Boolean isSuccess;
}