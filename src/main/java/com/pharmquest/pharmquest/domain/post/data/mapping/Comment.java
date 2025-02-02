package com.pharmquest.pharmquest.domain.post.data.mapping;

import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.domain.user.data.User;
import com.pharmquest.pharmquest.global.data.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 600)
    private String content;

    private boolean isDeleted = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent; // 부모 댓글

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "top_parent_id")
    private Comment topParent; // 최상위 부모

    @OneToMany(mappedBy = "topParent", cascade = CascadeType.ALL)
    private List<Comment> children = new ArrayList<>(); // 직계 자식

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    private List<CommentLike> likes = new ArrayList<>();

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    private List<CommentReport> reports = new ArrayList<>();


}
