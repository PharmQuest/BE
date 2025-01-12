package com.pharmquest.pharmquest.post.repository;

import com.pharmquest.pharmquest.post.domain.Post;
import com.pharmquest.pharmquest.post.domain.enums.Category;
import com.pharmquest.pharmquest.post.domain.enums.Country;
import com.pharmquest.pharmquest.user.domain.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final EntityManager entityManager;

    @Override
    public void createPost(Long userId, String title, String content, Country country, Category category) {
        User user = entityManager.getReference(User.class, userId);

        Post post = Post.builder()
                .user(user)
                .title(title)
                .content(content)
                .category(category)
                .country(country)
                .build();
        entityManager.persist(post);
    }
}
