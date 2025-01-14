package com.pharmquest.pharmquest.global.auth;

// 시큐리티가 /login 주소 요청이 오면 낚아채서 로그인을 진행시킨다.
// 로그인을 진행이 완료가 되면 시큐리티 session을 만들어준다. (Security ContextHolder 이라는 키값에 세션 정보를 저장 시킴.)
//오브젝트 타입 -> Authentication 타입 객체
//Authentication 안에 User 정보가 있어야 됨.
//User 오브젝트 타입 -> UserDetail 타입 객체

//Security Session 영역안에 들어갈 수 있는 객체가 Authentication 객체이어야만 함.
// 그리고 이 객체 안에 들어가야 하는 유저 정보의 타입은 UserDetails이다.

import com.pharmquest.pharmquest.domain.user.data.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@RequiredArgsConstructor
public class PrincipalDetails implements OAuth2User{
    private User user; //콤포지션
    private Map<String,Object> attributes;

    //OAuth 로그인
    public PrincipalDetails(User user, Map<String,Object> attributes) {
        this.user = user;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    //해당 User의 권한을 리턴하는 곳
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }
}