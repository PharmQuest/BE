package com.pharmquest.pharmquest.domain.post.domain;

import com.pharmquest.pharmquest.domain.home.PostCategory;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 홈에 게시글 불러오는 데에 필요하여 작성했습니다.
    private String title;

    private Integer scrapCount;
    @CreatedDate
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private PostCategory category;

}