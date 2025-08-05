package com.example.hangeulhunters.application.user.dto;

import com.example.hangeulhunters.domain.common.constant.Gender;
import com.example.hangeulhunters.domain.user.constant.AuthProvider;
import com.example.hangeulhunters.domain.user.constant.KoreanLevel;
import com.example.hangeulhunters.domain.user.constant.UserRole;
import com.example.hangeulhunters.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사용자 정보 DTO")
public class UserDto {
    @Schema(description = "사용자 ID", required = true)
    private Long id;
    
    @Schema(description = "이메일", required = true)
    private String email;
    
    @Schema(description = "닉네임", required = true)
    private String nickname;

    @Schema(description = "성별", required = true)
    private Gender gender;

    @Schema(description = "생년월일", format = "date", required = true)
    private LocalDate birthDate;
    
    @Schema(description = "사용자 역할", required = true)
    private UserRole role;
    
    @Schema(description = "인증 제공자 (소셜 로그인 등)", nullable = true)
    private AuthProvider provider;

    @Schema(description = "한국어 능력 레벨", nullable = true)
    private KoreanLevel koreanLevel;

    @Schema(description = "프로필 이미지 URL", nullable = true)
    private String profileImageUrl;
    
    @Schema(description = "관심사 목록", required = true)
    private List<String> interests;

    public static UserDto fromEntity(User user, List<String> interests) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .gender(user.getGender())
                .birthDate(user.getBirthDate())
                .role(user.getRole())
                .provider(user.getProvider())
                .koreanLevel(user.getKoreanLevel())
                .profileImageUrl(user.getProfileImageUrl())
                .interests(interests)
                .build();
    }
    
    public static UserDto fromEntity(User user) {
        return fromEntity(user, List.of());
    }
}