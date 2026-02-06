package com.example.hangeulhunters.application.persona.dto;

import com.example.hangeulhunters.domain.common.constant.Gender;
import com.example.hangeulhunters.domain.persona.entity.AIPersona;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * AI 페르소나 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "AI 페르소나 정보 DTO")
public class AIPersonaDto {

    @Schema(description = "AI 페르소나 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long personaId;

    @Schema(description = "사용자 ID (소유)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long userId;

    @Schema(description = "이름", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "성별", requiredMode = Schema.RequiredMode.REQUIRED)
    private Gender gender;

    @Schema(description = "나이", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer age;

    @Schema(description = "ai 역할", requiredMode = Schema.RequiredMode.REQUIRED)
    private String aiRole;

    @Schema(description = "유저 역할", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userRole;

    @Schema(description = "설명", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    @Schema(description = "프로필 이미지 URL", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String profileImageUrl;

    @Schema(description = "목소리", requiredMode = Schema.RequiredMode.REQUIRED)
    private String voice;

    /**
     * 엔티티를 DTO로 변환
     *
     * @param aiPersona AI 페르소나 엔티티
     * @return AI 페르소나 DTO
     */
    public static AIPersonaDto fromEntity(AIPersona aiPersona) {
        return AIPersonaDto.builder()
                .personaId(aiPersona.getId())
                .userId(aiPersona.getUserId())
                .name(aiPersona.getName())
                .gender(aiPersona.getGender())
                .age(aiPersona.getAge())
                .aiRole(aiPersona.getAiRole())
                .userRole(aiPersona.getUserRole())
                .description(aiPersona.getDescription())
                .profileImageUrl(aiPersona.getProfileImageUrl())
                .voice(aiPersona.getVoice())
                .build();
    }
}