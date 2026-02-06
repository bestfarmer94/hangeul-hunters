package com.example.hangeulhunters.application.user.dto;

import com.example.hangeulhunters.domain.user.constant.KoreanLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * 사용자 프로필 업데이트 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사용자 프로필 업데이트 요청 DTO")
public class ProfileUpdateRequest {
    
    @Schema(description = "닉네임", example = "홍길동")
    private String nickname;
    
    @Past(message = "Birth date must be in the past")
    @Schema(description = "생년월일", example = "1990-01-01", format = "date")
    private LocalDate birthDate;
    
    @Schema(description = "한국어 능력 레벨", example = "BEGINNER")
    private KoreanLevel koreanLevel;
    
    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private String profileImageUrl;
    
    @Schema(description = "관심사 목록")
    private List<String> interests;
}