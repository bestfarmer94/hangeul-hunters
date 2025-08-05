package com.example.hangeulhunters.application.auth.dto;

import com.example.hangeulhunters.application.user.dto.UserDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "인증 응답 DTO")
public class AuthResponse {
    @Schema(description = "액세스 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", required = true)
    private String accessToken;
    
    @Schema(description = "리프레시 토큰", example = "550e8400-e29b-41d4-a716-446655440000", required = true)
    private String refreshToken;
    
    @Schema(description = "사용자 정보", required = true)
    private UserDto user;
}