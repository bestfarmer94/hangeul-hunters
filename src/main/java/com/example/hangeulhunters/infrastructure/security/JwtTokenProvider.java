package com.example.hangeulhunters.infrastructure.security;

import com.example.hangeulhunters.infrastructure.config.JwtProperties;
import com.example.hangeulhunters.infrastructure.repository.RedisRefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    private final Key key;
    private final Long accessTokenExpirationMs;
    private final Long refreshTokenExpirationMs;
    private final UserDetailsServiceImpl userDetailsService;
    private final RedisRefreshTokenRepository refreshTokenRepository;

    public JwtTokenProvider(
            JwtProperties jwtProperties,
            UserDetailsServiceImpl userDetailsService,
            RedisRefreshTokenRepository refreshTokenRepository) {
        String secretKey = jwtProperties.getSecret();
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.accessTokenExpirationMs = jwtProperties.getAccessTokenExpiration();
        this.refreshTokenExpirationMs = jwtProperties.getRefreshTokenExpiration();;
        this.userDetailsService = userDetailsService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public String createToken(String username) {
        Claims claims = Jwts.claims().setSubject(username);
        Date now = new Date();
        Date validity = new Date(now.getTime() + accessTokenExpirationMs);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    
    /**
     * 리프레시 토큰 생성 (JWT 형식)
     *
     * @param email 사용자 이메일
     * @return 생성된 JWT 리프레시 토큰
     */
    public String createRefreshToken(String email) {
        // 리프레시 토큰을 위한 Claims 생성
        Claims claims = Jwts.claims().setSubject(email);
        // 추가 식별자 추가 (무작위성 부여)
        claims.put("tokenId", UUID.randomUUID().toString());
        
        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshTokenExpirationMs);
        
        // JWT 형식의 리프레시 토큰 생성
        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        
        // Redis에 저장 (기존 토큰은 자동으로 삭제됨)
        refreshTokenRepository.save(refreshToken, email, refreshTokenExpirationMs);
        
        return refreshToken;
    }
    
    /**
     * 리프레시 토큰 검증 후 새 액세스 토큰 발급
     *
     * @param refreshToken 리프레시 토큰
     * @return 새 액세스 토큰 (검증 실패 시 null)
     */
    public String refreshAccessToken(String refreshToken) {
        try {
            // JWT 형식 검증
            if (!validateToken(refreshToken)) {
                return null;
            }
            
            // 토큰에서 이메일 추출
            String email = getUsername(refreshToken);
            
            // Redis에 저장된 토큰과 비교 검증
            if (!refreshTokenRepository.validateToken(refreshToken, email)) {
                return null;
            }
            
            // 새 액세스 토큰 발급
            return createToken(email);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 리프레시 토큰 무효화
     *
     * @param email 사용자 이메일
     */
    public void invalidateRefreshToken(String email) {
        refreshTokenRepository.deleteByEmail(email);
    }
}