package com.example.hangeulhunters.application.interest.dto;

import com.example.hangeulhunters.domain.interest.entity.Interest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 관심사 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "관심사 정보 DTO")
public class InterestDto {
    
    @Schema(description = "관심사 ID", required = true)
    private Long id;
    
    @Schema(description = "사용자 ID", nullable = true)
    private Long userId;
    
    @Schema(description = "페르소나 ID", nullable = true)
    private Long personaId;
    
    @Schema(description = "관심사 이름", required = true)
    private String name;
    
    @Schema(description = "관심사 설명", nullable = true)
    private String description;
    
    public static InterestDto fromEntity(Interest interest) {
        return InterestDto.builder()
                .id(interest.getId())
                .userId(interest.getUserId())
                .personaId(interest.getPersonaId())
                .name(interest.getName())
                .description(interest.getDescription())
                .build();
    }
}