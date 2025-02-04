package com.pharmquest.pharmquest.domain.post.service.like;

import com.pharmquest.pharmquest.domain.post.converter.PostLikeConverter;
import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.domain.post.data.mapping.PostLike;
import com.pharmquest.pharmquest.domain.post.repository.like.PostLikeRepository;
import com.pharmquest.pharmquest.domain.post.repository.post.PostRepository;
import com.pharmquest.pharmquest.domain.user.data.User;
import com.pharmquest.pharmquest.domain.user.repository.UserRepository;
import com.pharmquest.pharmquest.global.apiPayload.code.status.ErrorStatus;
import com.pharmquest.pharmquest.global.apiPayload.exception.handler.PostHandler;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostLikeServiceImpl implements PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    @Override
    public PostLike createPostLike(Long userId, Long postId) {

        Optional<PostLike> postLike =
                postLikeRepository.findByPostIdAndUserId(postId, userId);

        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);

        Post post = postRepository.findById(postId).orElseThrow(EntityNotFoundException::new);

        if (postLike.isPresent()) {
            throw new PostHandler(ErrorStatus.POST_LIKE_ALREADY_EXISTS);
        }

        // 좋아요가 없으면 새로 추가
        return postLikeRepository.save(PostLikeConverter.toPostLike(user, post));


    }

    @Override
    public void deletePostLike(Long userId, Long postId) {

        Optional<PostLike> postLike =
                postLikeRepository.findByPostIdAndUserId(postId, userId);

        if (postLike.isEmpty()) {
            throw new PostHandler(ErrorStatus.POST_LIKE_NOT_EXIST);
        }

        postLikeRepository.delete(postLike.get());

    }
}
