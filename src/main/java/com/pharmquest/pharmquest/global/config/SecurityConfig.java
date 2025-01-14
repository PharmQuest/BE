package com.pharmquest.pharmquest.global.config;


import com.pharmquest.pharmquest.global.config.oauth.PrincipalOauth2UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final PrincipalOauth2UserService principalOauth2UserService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizeRequest ->
                        authorizeRequest
                                //.requestMatchers("/").authenticated() 경로에 접근할 때 인증이 필요한 사이트들 설정. 나중에 추가 예정
                                .anyRequest().permitAll()
                )
                .exceptionHandling(handler -> handler.authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException, IOException {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "UnAuthorized");
                    }
                }))
                .headers(
                        headersConfigurer ->
                                headersConfigurer
                                        .frameOptions(
                                                HeadersConfigurer.FrameOptionsConfig::sameOrigin
                                        )
                );

        http
                .formLogin(form -> form
                                .loginPage("/loginForm")
                                .loginProcessingUrl("/login")//login 주소가 호출이 되면 시큐리티가 낚아채서 대신 로그인을 해줌
                                .defaultSuccessUrl("/"));//로그인 성공시 기본 페이지
        http
                .oauth2Login(oauth2-> oauth2
                        .loginPage("/loginForm")
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                                .userService(principalOauth2UserService)));



        return http.build();
    }
}
