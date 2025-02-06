package com.pharmquest.pharmquest.domain.post.service.post;

import com.pharmquest.pharmquest.domain.post.converter.PostCommentConverter;
import com.pharmquest.pharmquest.domain.post.converter.PostConverter;
import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.domain.post.data.enums.Country;
import com.pharmquest.pharmquest.domain.post.data.enums.PostCategory;
import com.pharmquest.pharmquest.domain.post.data.mapping.Comment;
import com.pharmquest.pharmquest.domain.post.repository.bestPost.BestPostRepository;
import com.pharmquest.pharmquest.domain.post.repository.comment.CommentLikeRepository;
import com.pharmquest.pharmquest.domain.post.repository.comment.PostCommentRepository;
import com.pharmquest.pharmquest.domain.post.repository.like.PostLikeRepository;
import com.pharmquest.pharmquest.domain.post.repository.post.PostRepository;
import com.pharmquest.pharmquest.domain.post.repository.scrap.PostScrapRepository;
import com.pharmquest.pharmquest.domain.post.specification.PostSpecification;
import com.pharmquest.pharmquest.domain.post.web.dto.CommentResponseDTO;
import com.pharmquest.pharmquest.domain.post.web.dto.PostRequestDTO;
import com.pharmquest.pharmquest.domain.post.web.dto.PostResponseDTO;
import com.pharmquest.pharmquest.domain.user.data.User;
import com.pharmquest.pharmquest.domain.user.repository.UserRepository;
import com.pharmquest.pharmquest.global.apiPayload.code.status.ErrorStatus;
import com.pharmquest.pharmquest.global.apiPayload.exception.handler.PostHandler;
import com.pharmquest.pharmquest.global.service.S3Service;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostCommandServiceImpl implements PostCommandService {

    private final PostRepository postRepository;
    private final PostLikeRepository likeRepository;
    private final PostScrapRepository scrapRepository;
    private final PostCommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final BestPostRepository bestPostRepository;
    private final S3Service s3Service;
    private final PostConverter postConverter;

    private final UserRepository userRepository;

    //게시글 등록
    @Override
    public Post registerPost(Long userId, PostRequestDTO.CreatePostDTO request, MultipartFile imageFile) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 유저를 찾을 수 없습니다. ID: " + userId));

        String imageUrl = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            imageUrl = s3Service.uploadPostImg(imageFile);
        }

        if (request.getTitle() == null || request.getTitle().isEmpty()) {
            throw new PostHandler(ErrorStatus.TITLE_NOT_PROVIDED);
        }
        if (request.getContent() == null || request.getContent().isEmpty()) {
            throw new PostHandler(ErrorStatus.CONTENT_NOT_PROVIDED);
        }

        Post newPost = PostConverter.toPost(request,imageUrl);
        newPost.setUser(user);
        return postRepository.save(newPost);
    }

    //게시글 리스트 가져오기 (카테고리별 필터링, 20개씩 페이징)
    @Override
    public Page<Post> getAllPosts(Long userId, PostCategory category, Integer page) {

        PageRequest pageRequest = PageRequest.of(page - 1, 20, Sort.by(Sort.Direction.DESC, "createdAt"));

        if (category == PostCategory.ALL) {
            // category가 ALL이면 전체 게시물 조회
            if (userId == null) {
                // 로그인하지 않은 경우: 삭제된 게시글만 제외
                return postRepository.findAllActivePosts(pageRequest);
            } else {
                // 로그인한 경우: 삭제된 게시글과 신고한 게시글 모두 제외
                return postRepository.findAllVisiblePostsExcludingReportedByUser(userId,pageRequest);
            }
        } else {
            // 특정 category에 맞는 게시물 조회
            if (userId == null) {
                // 로그인하지 않은 경우: 삭제된 게시글만 제외
                return postRepository.findByCategoryExcludingDeletedPosts(category, pageRequest);
            } else {
                // 로그인한 경우: 삭제된 게시글과 신고한 게시글 제외
                return postRepository.findByCategoryExcludingReportedPosts(category, userId, pageRequest);
            }
        }
    }



    //게시글 상세보기
    @Override
    public PostResponseDTO.PostDetailDTO getPost(Long userId, Long postId, Integer page) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostHandler(ErrorStatus.POST_NOT_EXIST));

        boolean isLiked = likeRepository.existsByPostIdAndUserId(postId, userId);
        boolean isScrapped = scrapRepository.existsByPostIdAndUserId(postId, userId);
        boolean isOwnPost = userId.equals(post.getUser().getId());
        boolean isBestPost = bestPostRepository.existsByPostId(postId);

        // 최상위 댓글 페이지네이션
        Page<Comment> parentCommentsPage = commentRepository.findByPostAndParentIsNull(
                post,
                PageRequest.of(page - 1, 5)
        );

        // 최상위 댓글 처리
        List<CommentResponseDTO.CommentDTO> topLevelComments = parentCommentsPage.getContent().stream()
                .map(comment -> {

                    boolean isCommentLiked = commentLikeRepository.existsByCommentIdAndUserId(comment.getId(), userId);
                    boolean isOwnComment = userId.equals(comment.getUser().getId());
                    boolean isPostAuthor = post.getUser().getId().equals(comment.getUser().getId());

                    List<CommentResponseDTO.CommentDTO> replies = comment.getChildren().stream()
                            .map(child -> {
                                boolean isChildLiked = commentLikeRepository.existsByCommentIdAndUserId(child.getId(), userId);
                                boolean isChildOwnComment = userId.equals(child.getUser().getId());
                                boolean isChildPostAuthor = post.getUser().getId().equals(child.getUser().getId());
                                return PostCommentConverter.toComment(child, isChildLiked, isChildPostAuthor, isChildOwnComment);
                            })
                            .collect(Collectors.toList());

                    return PostCommentConverter.toComment(comment, isCommentLiked, isPostAuthor, isOwnComment)
                            .toBuilder()
                            .replies(replies)
                            .build();
                })
                .collect(Collectors.toList());

        return PostConverter.postDetailDTO(post, isLiked, isScrapped, isOwnPost, isBestPost, topLevelComments, parentCommentsPage);
    }

    //게시글 제목, 내용으로 검색(카테고리, 나라 별 필터링, 20개씩 페이징)
    @Override
    public Page<Post> searchPostsDynamically(Long userId, String keyword, Country country, PostCategory category, Integer page) {

        PageRequest pageRequest = PageRequest.of(page - 1, 20, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Post> posts = postRepository.findAll(
                PostSpecification.dynamicQuery(userId, keyword, category, country),
                pageRequest
        );

        if (posts.isEmpty()) {
            throw new PostHandler(ErrorStatus.POST_NOT_EXIST);
        }
        return posts;
    }

    @Override
    public void deletePost(Long userId, List<Long> postIds) {

        for(Long postId : postIds) {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new PostHandler(ErrorStatus.POST_NOT_EXIST));

            if (!userId.equals(post.getUser().getId())) {
                throw new PostHandler(ErrorStatus.NOT_POST_AUTHOR);
            }
            // 게시글 삭제
            postRepository.deleteById(postId);
        }
    }

    @Override
    @Transactional
    public Post updatePost(Long userId, Long postId, PostRequestDTO.UpdatePostDTO request, MultipartFile imageFile) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostHandler(ErrorStatus.POST_NOT_EXIST));

        if (!userId.equals(post.getUser().getId())) {
            throw new PostHandler(ErrorStatus.NOT_POST_AUTHOR);
        }

        if (request.getTitle() != null && !request.getTitle().isEmpty()) {
            post.setTitle(request.getTitle());
        }
        if (request.getContent() != null && !request.getContent().isEmpty()) {
            post.setContent(request.getContent());
        }
        if (request.getCategory() != null) {
            post.setCategory(request.getCategory());
        }

        String imageUrl = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            imageUrl = s3Service.uploadPostImg(imageFile);
            post.setPostImgURL(imageUrl);
        }

        //이미지를 삭제하고 싶을 때
        if (request.getDeleteImgae() && imageFile == null) {
            post.setPostImgURL(null);
        }

        return post;
    }



}
