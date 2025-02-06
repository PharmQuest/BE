package com.pharmquest.pharmquest.domain.post.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "best_post")
public class BestPost {
    @Id
    private Long id;  // Post의 id와 동일하게 설정

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "post_id")
    private Post post;

    private LocalDateTime bestPostAt;
}