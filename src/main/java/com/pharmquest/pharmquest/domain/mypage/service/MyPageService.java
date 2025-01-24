package com.pharmquest.pharmquest.domain.mypage.service;

import com.pharmquest.pharmquest.domain.post.data.Post;
import com.pharmquest.pharmquest.domain.post.web.dto.PostRequestDTO;
import com.pharmquest.pharmquest.domain.supplements.data.Supplements;

import java.util.List;

public interface MyPageService {

    List<Supplements> getScrapSupplements(Long userId);

}
