package com.example.hangeulhunters.application.persona.dto;

import com.example.hangeulhunters.domain.common.constant.Gender;
import com.example.hangeulhunters.domain.persona.constant.Relationship;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * AI 페르소나 생성/수정 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "AI 페르소나 생성/수정 요청 DTO")
public class AIPersonaRequest {

    @NotBlank(message = "이름은 필수입니다")
    @Schema(description = "이름", example = "한글이")
    private String name;

    @NotNull(message = "성별은 필수입니다")
    @Schema(description = "성별", example = "FEMALE")
    private Gender gender;

    @Min(value = 1, message = "나이는 1세 이상이어야 합니다")
    @Max(value = 120, message = "나이는 120세 이하여야 합니다")
    @Schema(description = "나이", example = "25")
    private Integer age;

    @Schema(description = "설명", example = "회사 사장님")
    private String description;

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/image.jpg")
    private String profileImageUrl;

    @NotBlank(message = "유저 역할은 필수입니다")
    @Schema(description = "유저 역할", example = "Junior employee in a Korean company")
    private String userRole;
}