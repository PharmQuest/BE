package com.pharmquest.pharmquest.domain.post.service.like;

import com.pharmquest.pharmquest.domain.post.converter.PostLikeConverter;
import com.pharmquest.pharmquest.domain.post.data.BestPost;
import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.domain.post.data.mapping.PostLike;
import com.pharmquest.pharmquest.domain.post.repository.bestPost.BestPostRepository;
import com.pharmquest.pharmquest.domain.post.repository.like.PostLikeRepository;
import com.pharmquest.pharmquest.domain.post.repository.post.PostRepository;
import com.pharmquest.pharmquest.domain.user.data.User;
import com.pharmquest.pharmquest.domain.user.repository.UserRepository;
import com.pharmquest.pharmquest.global.apiPayload.code.status.ErrorStatus;
import com.pharmquest.pharmquest.global.apiPayload.exception.handler.PostHandler;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostLikeServiceImpl implements PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final BestPostRepository bestPostRepository;

    @Transactional
    @Override
    public PostLike createPostLike(Long userId, Long postId) {
        if (postLikeRepository.findByPostIdAndUserId(postId, userId).isPresent()) {
            throw new PostHandler(ErrorStatus.POST_LIKE_ALREADY_EXISTS);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        // 좋아요 추가
        PostLike postLike = PostLikeConverter.toPostLike(user, post);
        postLikeRepository.save(postLike);

        // 좋아요 개수 확인 및 BestPost 등록 (트랜잭션 내에서 실행)
        int likeCount = postLikeRepository.countByPostId(postId);
        if (likeCount >= 10 && !bestPostRepository.existsByPostId(postId)) {
            BestPost newBestPost = new BestPost();
            newBestPost.setPost(post);
            newBestPost.setBestPostAt(LocalDateTime.now());
            bestPostRepository.save(newBestPost);
        }

        return postLike;
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
