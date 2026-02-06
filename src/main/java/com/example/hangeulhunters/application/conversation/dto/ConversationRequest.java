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
    @Schema(description = "대화 주제 ID", example = "1")
    private Long conversationTopicId;

    @NotBlank(message = "사용자 역할은 필수입니다")
    @Schema(description = "사용자 역할", example = "학생")
    private String userRole;

    @NotBlank(message = "AI 역할은 필수입니다")
    @Schema(description = "AI 역할", example = "선생님")
    private String aiRole;

    @NotBlank(message = "친밀도는 필수입니다")
    @Schema(description = "친밀도", example = "격식")
    private String closeness;

    @Schema(description = "상황 설명", example = "학교에서 선생님과 대화합니다")
    private String situation;
}