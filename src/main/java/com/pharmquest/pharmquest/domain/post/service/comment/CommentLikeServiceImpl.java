package com.pharmquest.pharmquest.domain.post.service.comment;

import com.pharmquest.pharmquest.domain.post.converter.PostCommentConverter;
import com.pharmquest.pharmquest.domain.post.data.mapping.Comment;
import com.pharmquest.pharmquest.domain.post.data.mapping.CommentLike;
import com.pharmquest.pharmquest.domain.post.repository.comment.CommentLikeRepository;
import com.pharmquest.pharmquest.domain.post.repository.comment.PostCommentRepository;
import com.pharmquest.pharmquest.domain.post.repository.post.PostRepository;
import com.pharmquest.pharmquest.domain.user.data.User;
import com.pharmquest.pharmquest.domain.user.repository.UserRepository;
import com.pharmquest.pharmquest.global.apiPayload.code.status.ErrorStatus;
import com.pharmquest.pharmquest.global.apiPayload.exception.handler.CommentHandler;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentLikeServiceImpl implements CommentLikeService {

    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;
    private final PostCommentRepository commentRepository;

    @Override
    public CommentLike createCommentLike(Long userId, Long commentId) {

        Optional<CommentLike> commentLike =
                commentLikeRepository.findByCommentIdAndUserId(commentId, userId);

        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);

        Comment comment = commentRepository.findById(commentId).orElseThrow(EntityNotFoundException::new);
        System.out.println("찾은 CommentLike: " + commentLike);  // 디버깅 출력

        if (commentLike.isPresent()) {
            throw new CommentHandler(ErrorStatus.COMMENT_LIKE_ALREADY_EXISTS);
        }

        // 좋아요가 없으면 새로 추가
        return commentLikeRepository.save(PostCommentConverter.toCommentLike(user, comment));


    }

    @Override
    public void deleteCommentLike(Long userId, Long commentId) {

        Optional<CommentLike> commentLike =
                commentLikeRepository.findByCommentIdAndUserId(commentId,userId);

        if (commentLike.isEmpty()) {
            throw new CommentHandler(ErrorStatus.COMMENT_LIKE_NOT_EXIST);
        }

        commentLikeRepository.delete(commentLike.get());

    }
}