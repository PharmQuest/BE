package com.pharmquest.pharmquest.domain.post.repository.post;

import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.domain.post.data.enums.Country;
import com.pharmquest.pharmquest.domain.post.data.enums.PostCategory;
import com.pharmquest.pharmquest.user.domain.User;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public void createPost(Long userId, String title, String content, Country country, PostCategory category) {
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
