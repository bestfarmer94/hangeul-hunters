package com.example.hangeulhunters.application.conversation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ASK 타입 대화 생성 요청 DTO
 * 페르소나 없이 질문을 위한 대화를 생성합니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "ASK 타입 대화 생성 요청")
public class AskRequest {

    @NotBlank(message = "질문 대상은 필수입니다")
    @Schema(description = "질문 대상", example = "My manager")
    private String askTarget;

    @NotBlank(message = "질문 대상과의 친밀도는 필수입니다")
    @Schema(description = "질문 대상과의 친밀도", example = "Professional")
    private String closeness;

    @NotBlank(message = "상황 설명은 필수입니다")
    @Schema(description = "상황 설명", example = "I'm late for our meeting and want to say sorry.")
    private String situation;
}
