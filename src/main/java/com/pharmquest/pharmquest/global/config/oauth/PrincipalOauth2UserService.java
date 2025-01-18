package com.pharmquest.pharmquest.global.config.oauth;

import com.pharmquest.pharmquest.global.auth.PrincipalDetails;
import com.pharmquest.pharmquest.global.config.oauth.provider.KakaoUserInfo;
import com.pharmquest.pharmquest.global.config.oauth.provider.OAuth2UserInfo;
import com.pharmquest.pharmquest.global.config.oauth.provider.GoogleUserInfo;
import com.pharmquest.pharmquest.global.config.oauth.provider.NaverUserInfo;
import com.pharmquest.pharmquest.domain.user.data.User;
import com.pharmquest.pharmquest.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service
@RequiredArgsConstructor
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        OAuth2UserInfo oAuth2UserInfo = null;
        if(userRequest.getClientRegistration().getRegistrationId().equals("google")) {
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        } else if(userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
            oAuth2UserInfo = new NaverUserInfo((Map)oAuth2User.getAttributes().get("response"));
        }    else if(userRequest.getClientRegistration().getRegistrationId().equals("kakao")){	//추가
            oAuth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());
        }

        String provider = oAuth2UserInfo.getProvider();
        String providerId = oAuth2UserInfo.getProviderId();
        String username = oAuth2UserInfo.getName();
        String email = oAuth2UserInfo.getEmail();

        System.out.println(userRequest.getAccessToken());
        System.out.println("Provider ID: " + providerId);
        System.out.println("Username: " + username);
        System.out.println("Email: " + email);



        User userEntity = userRepository.findByName(providerId);

        if(userEntity == null) {
            userEntity = User.builder()
                    .name(username)
                    .providerId(providerId)
                    .provider(provider)
                    .email(email)
                    .build();
            userRepository.save(userEntity);
        }
        return new PrincipalDetails(userEntity, oAuth2User.getAttributes());
    }
}
