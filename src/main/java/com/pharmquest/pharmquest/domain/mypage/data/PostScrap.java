package com.pharmquest.pharmquest.domain.mypage.data;

import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.domain.user.data.User;
import com.pharmquest.pharmquest.global.data.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PostScrap extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
}