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
    
    @Schema(description = "AI 페르소나 ID", required = true)
    private Long personaId;
    
    @Schema(description = "사용자 ID (소유)", nullable = true)
    private Long userId;
    
    @Schema(description = "이름", required = true)
    private String name;
    
    @Schema(description = "성별", required = true)
    private Gender gender;
    
    @Schema(description = "나이", required = true)
    private Integer age;
    
    @Schema(description = "관계", required = true)
    private String relationship;
    
    @Schema(description = "설명", nullable = true)
    private String description;
    
    @Schema(description = "프로필 이미지 URL", nullable = true)
    private String profileImageUrl;
    
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
                .relationship(aiPersona.getRelationship())
                .description(aiPersona.getDescription())
                .profileImageUrl(aiPersona.getProfileImageUrl())
                .build();
    }
}