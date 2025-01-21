package com.pharmquest.pharmquest.domain.token.service;

import com.pharmquest.pharmquest.domain.token.web.dto.TokenResponseDTO;
import org.springframework.stereotype.Service;

@Service
public interface TokenService {
    TokenResponseDTO reissueAccessToken(String authorizationHeader);
}