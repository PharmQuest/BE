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
    public Page<Post> getBestPosts(Integer page) {

        PageRequest pageRequest = PageRequest.of(page - 1, 20, Sort.by(Sort.Direction.DESC, "createdAt"));

        return bestPostRepository.findBestPosts(pageRequest);
    }

    public List<Post> getRandomBestPosts(int count) {
        List<Long> randomIds = bestPostRepository.findRandomPostIds(count);
        return postRepository.findByIdIn(randomIds);
    }
}
