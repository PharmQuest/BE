package com.pharmquest.pharmquest.post.domain;

import com.pharmquest.pharmquest.post.domain.common.BaseEntity;
import com.pharmquest.pharmquest.post.domain.enums.Category;
import com.pharmquest.pharmquest.post.domain.enums.Country;
import com.pharmquest.pharmquest.user.domain.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Getter
@Setter
@DynamicUpdate
@DynamicInsert
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length =100 )
    private String title;

    @Column(nullable = false, length = 4000)
    private String content;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(15) DEFAULT 'KOREA'")
    private Country country;

    @ColumnDefault("0")
    private Integer views;

    @ColumnDefault("0")
    private Integer reports;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;





}