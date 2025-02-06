package com.pharmquest.pharmquest.domain.post.service.scrap;


import com.pharmquest.pharmquest.domain.mypage.data.PostScrap;
import com.pharmquest.pharmquest.domain.post.converter.PostScrapConverter;
import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.domain.post.repository.post.PostRepository;
import com.pharmquest.pharmquest.domain.post.repository.scrap.PostScrapRepository;
import com.pharmquest.pharmquest.domain.user.data.User;
import com.pharmquest.pharmquest.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostScrapServiceImpl implements PostScrapService {

    private final PostScrapRepository postScrapRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Override
    public PostScrap createPostScrap(Long userId, Long postId) {

        Optional<PostScrap> postScrap =
                postScrapRepository.findByPostIdAndUserId(postId, userId);

        User user = userRepository.findById(userId).orElseThrow(EntityNotFoundException::new);

        Post post = postRepository.findById(postId).orElseThrow(EntityNotFoundException::new);

        if (postScrap.isPresent()) {
            throw new IllegalStateException("이미 스크랩한 게시물 입니다.");
        }

        // 스크랩이 없으면 새로 추가
        return postScrapRepository.save(PostScrapConverter.toPostScrap(user, post));

    }

    @Override
    public void deletePostScrap(Long userId, List<Long> postIds) {

        for (Long postId : postIds) {
            Optional<PostScrap> postScrap =
                    postScrapRepository.findByPostIdAndUserId(postId, userId);

            if (postScrap.isEmpty()) {
                throw new IllegalStateException("스크랩하지 않은 게시물 입니다. id : " + postIds);
            }

            postScrapRepository.delete(postScrap.get());
        }
    }
}

