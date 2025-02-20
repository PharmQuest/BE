package com.pharmquest.pharmquest.global.apiPayload.exception.handler;

import com.pharmquest.pharmquest.domain.token.JwtUtil;
import com.pharmquest.pharmquest.domain.token.data.RefreshToken;
import com.pharmquest.pharmquest.domain.token.repository.RefreshTokenRepository;
import com.pharmquest.pharmquest.domain.user.data.User;
import com.pharmquest.pharmquest.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TempLoginHandler { // 임시 로그인 Handler

    private final long ACCESS_TOKEN_EXPIRATION_TIME = 360000000; // 액세스 토큰 유효기간 ( 다시 로그인 안 해도 되게 만료기간 따로 설정 )

    @Value("${jwt.refresh-token.expiration-time}")
    private long REFRESH_TOKEN_EXPIRATION_TIME; // 리프레쉬 토큰 유효기간

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public void tempLogin(HttpServletResponse response, String name) throws IOException {

        User existUser = userRepository.findByName(name);

        User user;
        if(existUser == null) {
            log.info("신규 임시 유저입니다. 등록을 진행합니다.");
            user = User.builder()
                    .userId(UUID.randomUUID())
                    .email(UUID.randomUUID().toString().substring(0,8) + "@pharmquest.com") // email은 not null 설정 되어있음. 중복되면 로그인이 안 되길래 UUID 박아버림
                    .name(name)
                    .provider("local")
                    .providerId(null)
                    .build();
            userRepository.save(user);
        }else{
            // 기존 유저인 경우
            log.info("기존 임시 유저입니다.");
            refreshTokenRepository.deleteByUserId(existUser.getUserId());
            user = existUser;
        }

        log.info("임시 유저 이름 : {}", user.getName());

        // 리프레쉬 토큰 발급 후 저장
        String refreshToken = jwtUtil.generateRefreshToken(user.getUserId(), REFRESH_TOKEN_EXPIRATION_TIME);

        RefreshToken newRefreshToken = RefreshToken.builder()
                .userId(user.getUserId())
                .token(refreshToken)
                .build();
        refreshTokenRepository.save(newRefreshToken);

        // 액세스 토큰 발급
        String accessToken = jwtUtil.generateAccessToken(user.getUserId(), ACCESS_TOKEN_EXPIRATION_TIME);

        String jsonResponse = String.format("{\"message\": \"임시 로그인 성공!\", \"name\": \"%s\", \"accessToken\": \"%s\", \"refreshToken\": \"%s\"}",
                user.getName(), accessToken, refreshToken);


        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(jsonResponse);
    }

}
