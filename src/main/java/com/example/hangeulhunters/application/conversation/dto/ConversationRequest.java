package com.example.hangeulhunters.application.conversation.dto;

import com.example.hangeulhunters.domain.conversation.constant.SituationExample;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationRequest {
    @NotNull(message = "AI 페르소나 ID는 필수입니다.")
    @Schema(description = "AI 페르소나 ID", example = "1")
    private Long personaId;

    @Schema(description = "대화 상황", example = "BOSS1")
    private SituationExample situation;
}