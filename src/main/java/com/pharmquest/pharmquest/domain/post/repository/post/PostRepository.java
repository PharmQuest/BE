package com.pharmquest.pharmquest.domain.post.repository.post;

import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.domain.post.data.enums.Country;
import com.pharmquest.pharmquest.domain.post.data.enums.PostCategory;
import com.pharmquest.pharmquest.domain.post.specification.PostSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import reactor.util.annotation.NonNull;
import reactor.util.annotation.NonNullApi;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom, JpaSpecificationExecutor<Post> {

    Page<Post> findByCategory(PostCategory category, Pageable pageable);


}
