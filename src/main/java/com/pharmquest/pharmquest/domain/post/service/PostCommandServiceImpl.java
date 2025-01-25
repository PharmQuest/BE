package com.pharmquest.pharmquest.domain.post.service;

import com.pharmquest.pharmquest.domain.post.converter.PostConverter;
import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.domain.post.data.enums.Country;
import com.pharmquest.pharmquest.domain.post.data.enums.PostCategory;
import com.pharmquest.pharmquest.domain.post.repository.like.PostLikeRepository;
import com.pharmquest.pharmquest.domain.post.repository.post.PostRepository;
import com.pharmquest.pharmquest.domain.post.repository.report.PostReportRepository;
import com.pharmquest.pharmquest.domain.post.repository.scrap.PostScrapRepository;
import com.pharmquest.pharmquest.domain.post.specification.PostSpecification;
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

@Service
@RequiredArgsConstructor
public class PostCommandServiceImpl implements PostCommandService{

    private final PostRepository postRepository;
    private final PostLikeRepository likeRepository;
    private final PostReportRepository reportRepository;
    private final PostScrapRepository scrapRepository;

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

    //게시글 리스트 가져오기 (카테고리별 필터링, 10개씩 페이징)
    @Override
    public Page<Post> getAllPosts(PostCategory category, Integer page) {

        PageRequest pageRequest = PageRequest.of(page - 1, 20, Sort.by(Sort.Direction.DESC, "createdAt"));

        if (category == PostCategory.ALL) {
            // category가 ALL이면 전체 게시물 조회
            return postRepository.findAll(pageRequest);
        } else {
            // 특정 category에 맞는 게시물 조회
            return postRepository.findByCategory(category, pageRequest);
        }
    }

    //게시글 상세보기
    @Override
    public PostResponseDTO.PostDetailDTO getPost(Long userId, Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(postId + "에 해당하는 게시글이 없습니다."));

        boolean isLiked = likeRepository.existsByPostIdAndUserId(postId, userId);
        boolean isReported = reportRepository.existsByPostIdAndUserId(postId, userId);
        boolean isScrapped = scrapRepository.existsByPostIdAndUserId(postId, userId);

        return PostConverter.postDetailDTO(post, isLiked, isScrapped, isReported);
    }

    //게시글 제목, 내용으로 검색(카테고리, 나라 별 필터링, 10개씩 페이징)
    @Override
    public Page<Post> searchPostsDynamically(String keyword, Country country, PostCategory category, Integer page) {

        PageRequest pageRequest = PageRequest.of(page - 1, 20, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Post> posts = postRepository.findAll(
                PostSpecification.dynamicQuery(keyword, category, country),
                pageRequest
        );

        if (posts.isEmpty()) {
            throw new EntityNotFoundException("검색어에 해당하는 게시글이 없습니다.");
        }
        return posts;
    }

}
