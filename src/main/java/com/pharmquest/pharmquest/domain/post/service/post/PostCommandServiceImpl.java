package com.pharmquest.pharmquest.domain.post.service.post;

import com.pharmquest.pharmquest.domain.post.converter.PostCommentConverter;
import com.pharmquest.pharmquest.domain.post.converter.PostConverter;
import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.domain.post.data.enums.Country;
import com.pharmquest.pharmquest.domain.post.data.enums.PostCategory;
import com.pharmquest.pharmquest.domain.post.data.mapping.Comment;
import com.pharmquest.pharmquest.domain.post.repository.comment.PostCommentRepository;
import com.pharmquest.pharmquest.domain.post.repository.like.PostLikeRepository;
import com.pharmquest.pharmquest.domain.post.repository.post.PostRepository;
import com.pharmquest.pharmquest.domain.post.repository.report.PostReportRepository;
import com.pharmquest.pharmquest.domain.post.repository.scrap.PostScrapRepository;
import com.pharmquest.pharmquest.domain.post.specification.PostSpecification;
import com.pharmquest.pharmquest.domain.post.web.dto.CommentResponseDTO;
import com.pharmquest.pharmquest.domain.post.web.dto.PostRequestDTO;
import com.pharmquest.pharmquest.domain.post.web.dto.PostResponseDTO;
import com.pharmquest.pharmquest.domain.user.data.User;
import com.pharmquest.pharmquest.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostCommandServiceImpl implements PostCommandService {

    private final PostRepository postRepository;
    private final PostLikeRepository likeRepository;
    private final PostReportRepository reportRepository;
    private final PostScrapRepository scrapRepository;
    private final PostCommentRepository commentRepository;

    private final UserRepository userRepository;

    //게시글 등록
    @Override
    public Post registerPost(Long userId, PostRequestDTO.CreatePostDTO request ) {

        Post newPost = PostConverter.toPost(request);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 유저를 찾을 수 없습니다. ID: " + userId));
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
                .orElseThrow(() -> new EntityNotFoundException(postId + "에 해당하는 게시글이 없습니다."));

        boolean isLiked = likeRepository.existsByPostIdAndUserId(postId, userId);
        boolean isReported = reportRepository.existsByPostIdAndUserId(postId, userId);
        boolean isScrapped = scrapRepository.existsByPostIdAndUserId(postId, userId);

        Page<Comment> parentCommentsPage = commentRepository.findByPostAndParentIsNull(
                post,
                PageRequest.of(page - 1, 5)
        );

        List<CommentResponseDTO.CommentDTO> topLevelComments = parentCommentsPage.getContent().stream()
                .map(PostCommentConverter::toComment)
                .collect(Collectors.toList());


        return PostConverter.postDetailDTO(post, isLiked, isScrapped, isReported, topLevelComments,parentCommentsPage);
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
            throw new EntityNotFoundException("검색어에 해당하는 게시글이 없습니다.");
        }
        return posts;
    }

    @Override
    public void deletePost(Long userId, Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("해당하는 게시글을 찾을 수 없습니다. ID: " + postId));

        if (!userId.equals(post.getUser().getId())) {
            throw new IllegalArgumentException("작성자만 게시글을 삭제할 수 있습니다.");
        }

        // 게시글 삭제
        postRepository.deleteById(postId);

    }

}
