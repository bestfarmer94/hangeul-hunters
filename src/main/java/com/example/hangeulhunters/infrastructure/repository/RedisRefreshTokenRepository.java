package com.example.hangeulhunters.infrastructure.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Redis를 사용한 리프레시 토큰 저장소
 */
@Repository
@RequiredArgsConstructor
public class RedisRefreshTokenRepository {

    private final StringRedisTemplate redisTemplate;
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";

    /**
     * 리프레시 토큰 저장
     *
     * @param token 토큰 문자열
     * @param email 사용자 이메일
     * @param expiryTimeMillis 만료 시간 (밀리초)
     */
    public void save(String token, String email, long expiryTimeMillis) {
        String refreshTokenKey = REFRESH_TOKEN_PREFIX + email;

        // 이메일로 토큰 조회 가능하도록 저장 (만료 시간과 함께 한 번에 설정)
        redisTemplate.opsForValue().set(refreshTokenKey, token, expiryTimeMillis, TimeUnit.MILLISECONDS);
    }

    /**
     * 이메일로 토큰 조회
     *
     * @param email 사용자 이메일
     * @return 토큰 문자열 (없으면 empty)
     */
    public Optional<String> findByEmail(String email) {
        String key = REFRESH_TOKEN_PREFIX + email;
        String token = redisTemplate.opsForValue().get(key);
        return Optional.ofNullable(token);
    }
    
    /**
     * 토큰 검증
     *
     * @param token 토큰 문자열
     * @param email 사용자 이메일
     * @return 검증 성공 여부
     */
    public boolean validateToken(String token, String email) {
        String storedToken = findByEmail(email).orElse(null);
        return token != null && token.equals(storedToken);
    }

    /**
     * 이메일로 토큰 삭제
     *
     * @param email 사용자 이메일
     */
    public void deleteByEmail(String email) {
        String emailKey = REFRESH_TOKEN_PREFIX + email;
        redisTemplate.delete(emailKey);
    }
}