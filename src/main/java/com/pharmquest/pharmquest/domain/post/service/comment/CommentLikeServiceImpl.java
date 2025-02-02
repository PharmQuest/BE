package com.pharmquest.pharmquest.domain.post.service.comment;

import com.pharmquest.pharmquest.domain.post.converter.PostCommentConverter;
import com.pharmquest.pharmquest.domain.post.converter.PostLikeConverter;
import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.domain.post.data.mapping.Comment;
import com.pharmquest.pharmquest.domain.post.data.mapping.CommentLike;
import com.pharmquest.pharmquest.domain.post.data.mapping.PostLike;
import com.pharmquest.pharmquest.domain.post.repository.comment.CommentLikeRepository;
import com.pharmquest.pharmquest.domain.post.repository.comment.PostCommentRepository;
import com.pharmquest.pharmquest.domain.post.repository.like.PostLikeRepository;
import com.pharmquest.pharmquest.domain.post.repository.post.PostRepository;
import com.pharmquest.pharmquest.domain.post.service.like.PostLikeService;
import com.pharmquest.pharmquest.domain.user.data.User;
import com.pharmquest.pharmquest.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentLikeServiceImpl implements CommentLikeService {

    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostCommentRepository commentRepository;

    @Override
    public CommentLike createCommentLike(Long userId, Long commentId) {

        Optional<CommentLike> commentLike =
                commentLikeRepository.findByCommentIdAndUserId(commentId, userId);

        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);

        Comment comment = commentRepository.findById(commentId).orElseThrow(EntityNotFoundException::new);

        if (commentLike.isPresent()) {
            throw new IllegalStateException("이미 좋아요를 누른 게시물 입니다.");
        }

        // 좋아요가 없으면 새로 추가
        return commentLikeRepository.save(PostCommentConverter.toCommentLike(user, comment));


    }

//    @Override
//    public void deletePostLike(Long userId, Long postId) {
//
//        Optional<PostLike> postLike =
//                postLikeRepository.findByPostIdAndUserId(postId, userId);
//
//        if (postLike.isEmpty()) {
//            throw new IllegalStateException("좋아요를 누르지 않은 게시물 입니다.");
//        }
//
//        postLikeRepository.delete(postLike.get());
//
//    }
}