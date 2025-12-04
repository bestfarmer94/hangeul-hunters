package com.example.hangeulhunters.application.conversation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationRequest {

    @NotBlank(message = "대화 주제는 필수입니다.")
    @Schema(description = "대화 주제", example = "After-Work Escape Mode")
    private String conversationTopic;

    @NotBlank(message = "AI 역할은 필수입니다.")
    @Schema(description = "AI 역할", example = "Your Korean team leader")
    private String aiRole;

    @NotBlank(message = "유저 역할은 필수입니다.")
    @Schema(description = "유저 역할", example = "Junior employee in a Korean company")
    private String userRole;

    @Schema(description = "부연 상황 설명")
    private String details;
}