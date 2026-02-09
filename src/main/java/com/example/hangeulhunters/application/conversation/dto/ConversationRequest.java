package com.example.hangeulhunters.application.conversation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "롤플레잉 대화 생성 요청")
public class ConversationRequest {

    @NotNull(message = "대화 주제 ID는 필수입니다")
    @Schema(description = "대화 주제 ID", example = "2")
    private Long conversationTopicId;

    @NotBlank(message = "사용자 역할은 필수입니다")
    @Schema(description = "사용자 역할", example = "A team member")
    private String userRole;

    @NotBlank(message = "AI 역할은 필수입니다")
    @Schema(description = "AI 역할", example = "A colleague")
    private String aiRole;

    @NotBlank(message = "친밀도는 필수입니다")
    @Schema(description = "친밀도", example = "Casual")
    private String closeness;

    @NotBlank(message = "상황 설명은 필수입니다")
    @Schema(description = "상황 설명", example = "You are working on a task you don’t fully understand.\n" +
            "The deadline is coming up, and you need to ask for clarification or support without sounding incompetent.")
    private String situation;
}