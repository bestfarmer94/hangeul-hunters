package com.example.hangeulhunters.application.auth.service;

import com.example.hangeulhunters.application.auth.dto.*;
import com.example.hangeulhunters.application.user.dto.UserDto;
import com.example.hangeulhunters.application.user.service.UserService;
import com.example.hangeulhunters.infrastructure.exception.UnauthorizedException;
import com.example.hangeulhunters.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 인증 관련 서비스
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    /**
     * 로그인 처리
     *
     * @param loginRequest 로그인 요청 정보
     * @return 인증 응답 (토큰 및 사용자 정보)
     */
    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {
        // 인증 처리
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        // 인증 정보 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        String email = loginRequest.getEmail();
        
        // JWT 액세스 토큰 생성
        String accessToken = jwtTokenProvider.createToken(email);
        
        // 리프레시 토큰 생성
        String refreshToken = jwtTokenProvider.createRefreshToken(email);
        
        // 사용자 정보 조회
        UserDto userDto = userService.getUserByEmail(email);
        
        // 응답 생성
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userDto)
                .build();
    }

    /**
     * 회원가입 처리
     *
     * @param signUpRequest 회원가입 요청 정보
     * @return 인증 응답 (토큰 및 사용자 정보)
     */
    @Transactional
    public AuthResponse signup(SignUpRequest signUpRequest) {
        // 사용자 생성
        UserDto userDto = userService.createUser(signUpRequest);
        
        String email = userDto.getEmail();
        
        // JWT 액세스 토큰 생성
        String accessToken = jwtTokenProvider.createToken(email);
        
        // 리프레시 토큰 생성
        String refreshToken = jwtTokenProvider.createRefreshToken(email);
        
        // 응답 생성
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(userDto)
                .build();
    }
    
    /**
     * 토큰 갱신 처리
     *
     * @param request 토큰 갱신 요청
     * @return 토큰 갱신 응답
     */
    @Transactional
    public TokenRefreshResponse refreshToken(TokenRefreshRequest request) {
        String refreshToken = request.getRefreshToken();
        String newAccessToken = jwtTokenProvider.refreshAccessToken(refreshToken);
        
        if (newAccessToken == null) {
        throw new UnauthorizedException("Invalid or expired refresh token");
        }
        
        return TokenRefreshResponse.builder()
                .accessToken(newAccessToken)
                .build();
    }
    
    /**
     * 로그아웃 처리
     *
     * @param userId 사용자 ID
     */
    @Transactional
    public void logout(Long userId) {
        // 사용자 정보 조회
        UserDto userDto = userService.getUserById(userId);

        jwtTokenProvider.invalidateRefreshToken(userDto.getEmail());
    }
}