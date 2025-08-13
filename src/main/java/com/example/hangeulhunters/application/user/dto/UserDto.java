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
    @Schema(description = "사용자 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;
    
    @Schema(description = "이메일", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;
    
    @Schema(description = "닉네임", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nickname;

    @Schema(description = "성별", requiredMode = Schema.RequiredMode.REQUIRED)
    private Gender gender;

    @Schema(description = "생년월일", format = "date", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate birthDate;
    
    @Schema(description = "사용자 역할", requiredMode = Schema.RequiredMode.REQUIRED)
    private UserRole role;
    
    @Schema(description = "인증 제공자 (소셜 로그인 등)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private AuthProvider provider;

    @Schema(description = "한국어 능력 레벨", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private KoreanLevel koreanLevel;

    @Schema(description = "학습한 문장 수", example = "215", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer sentenceCount;

    @Schema(description = "한국어 레벨 숫자값 (1-5)", example = "3", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer kLevel;

    @Schema(description = "프로필 이미지 URL", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String profileImageUrl;
    
    @Schema(description = "관심사 목록", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> interests;

    /**
     * User 엔티티를 UserDto로 변환합니다.
     *
     * @param user User 엔티티
     * @param interests 관심사 목록
     * @return UserDto 객체
     */
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

    /**
     * User 엔티티를 UserDto로 변환합니다.
     * 관심사 목록은 빈 리스트로 설정합니다.
     *
     * @param user User 엔티티
     * @return UserDto 객체
     */
    public static UserDto fromEntity(User user) {
        return fromEntity(user, List.of());
    }

    /**
     * 사용자의 학습 정보 추가
     *
     * @param sentenceCount 학습한 문장 수
     */
    public void addMyLearningInfo(Integer sentenceCount) {
        this.sentenceCount = sentenceCount;
        this.kLevel = calculateKLevelBySentenceCount(sentenceCount);
    }

    /**
     * 학습한 문장 수에 따라 한국어 레벨을 계산합니다.
     *
     * @param sentenceCount 학습한 문장 수
     * @return 한국어 레벨 (1-5)
     */
    public Integer calculateKLevelBySentenceCount(Integer sentenceCount) {
        if (sentenceCount < 50) {
            return 1;
        } else if (sentenceCount < 150) {
            return 2;
        } else if (sentenceCount < 300) {
            return 3;
        } else if (sentenceCount < 500) {
            return 4;
        } else {
            return 5;
        }
    }
}