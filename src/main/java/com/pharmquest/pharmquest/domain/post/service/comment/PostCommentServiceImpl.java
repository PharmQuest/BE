package com.pharmquest.pharmquest.domain.post.service.comment;

import com.pharmquest.pharmquest.domain.post.converter.PostCommentConverter;
import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.domain.post.data.mapping.Comment;
import com.pharmquest.pharmquest.domain.post.repository.comment.PostCommentRepository;
import com.pharmquest.pharmquest.domain.post.repository.post.PostRepository;
import com.pharmquest.pharmquest.domain.post.web.dto.CommentRequestDTO;
import com.pharmquest.pharmquest.domain.user.data.User;
import com.pharmquest.pharmquest.domain.user.repository.UserRepository;
import com.pharmquest.pharmquest.global.apiPayload.code.status.ErrorStatus;
import com.pharmquest.pharmquest.global.apiPayload.exception.handler.CommentHandler;
import com.pharmquest.pharmquest.global.apiPayload.exception.handler.PostHandler;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostCommentServiceImpl implements PostCommentService {

    private final PostCommentRepository postCommentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;


    @Override
    @Transactional
    public Comment addComment(Long userId, Long postId, Long parentId, CommentRequestDTO.CreateCommentDTO request) {

        Comment newComment = PostCommentConverter.toAddComment(request);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 유저를 찾을 수 없습니다. ID: " + userId));
                newComment.setUser(user);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostHandler(ErrorStatus.POST_NOT_EXIST));
                newComment.setPost(post);


        if (parentId != null) {
            Comment parentComment = postCommentRepository.findById(parentId)
                    .orElseThrow(() -> new CommentHandler(ErrorStatus.COMMENT_NOT_EXIST));

            if (!parentComment.getPost().getId().equals(postId)) {
                throw new CommentHandler(ErrorStatus.COMMENT_NOT_IN_POST);
            }

            newComment.setParent(parentComment);

            // 최상위 부모 댓글 찾기
            Comment topLevelParent = findTopLevelParent(parentComment);

            newComment.setTopParent(topLevelParent);

            //최상위 댓글의 자식 리스트에 추가
            topLevelParent.getChildren().add(newComment);

            return  postCommentRepository.save(newComment);
        } else {
            // 부모 댓글이 없으면 그냥 새 댓글만 저장
            return  postCommentRepository.save(newComment);
        }

    }

    // 최상위 부모 댓글 찾기
    private Comment findTopLevelParent(Comment comment) {
        while (comment.getParent() != null) {
            comment = comment.getParent();
        }
        return comment;
    }
}
