package com.pharmquest.pharmquest.domain.mypage.domain;

import com.pharmquest.pharmquest.domain.supplements.domain.Supplements;
import com.pharmquest.pharmquest.domain.user.domain.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class SupplementScrap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "supplement_id", referencedColumnName = "id", nullable = false)
    private Supplements supplement;
}

