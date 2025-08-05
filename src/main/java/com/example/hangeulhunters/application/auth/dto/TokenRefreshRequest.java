package com.example.hangeulhunters.application.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 토큰 갱신 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "토큰 갱신 요청 DTO")
public class TokenRefreshRequest {
    
    @NotBlank(message = "Refresh token is required")
    @Schema(description = "리프레시 토큰", example = "550e8400-e29b-41d4-a716-446655440000")
    private String refreshToken;
}