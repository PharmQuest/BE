package com.pharmquest.pharmquest.domain.post.service.bestPost;

import com.pharmquest.pharmquest.domain.post.data.BestPost;
import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.domain.post.repository.bestPost.BestPostRepository;
import com.pharmquest.pharmquest.domain.post.repository.post.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BestPostServiceImpl implements BestPostService {

    private final BestPostRepository bestPostRepository;
    private final PostRepository postRepository;


    @Override
    public Page<Post> getBestPosts(Long userId,Integer page) {

        PageRequest pageRequest = PageRequest.of(page - 1, 20, Sort.by(Sort.Direction.DESC, "createdAt"));

        if (userId == null) {
            // 로그인하지 않은 경우
            return bestPostRepository.findBestPosts(pageRequest);
        } else {
            // 로그인한 경우
            return bestPostRepository.findBestPostsByUser(userId, pageRequest);
        }

    }

    public List<Post> getRandomBestPosts(Long userId, int count) {

        List<Long> randomIds=null;
        if (userId == null) {
            randomIds = bestPostRepository.findRandomPostIds(count);

        }
        else{
            randomIds = bestPostRepository.findRandomPostIdsExcludingReportedByUser(userId, count);
        }

        return postRepository.findByIdIn(randomIds);
    }
}
