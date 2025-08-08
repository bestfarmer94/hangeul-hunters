package com.example.hangeulhunters.application.conversation.dto;

import com.example.hangeulhunters.application.persona.dto.AIPersonaDto;
import com.example.hangeulhunters.domain.conversation.constant.ConversationStatus;
import com.example.hangeulhunters.domain.conversation.entity.Conversation;
import com.example.hangeulhunters.infrastructure.util.DateTimeUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationDto {

    @Schema(description = "대화 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long conversationId;

    @Schema(description = "사용자 ID (소유)", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @Schema(description = "AI 정보", requiredMode = Schema.RequiredMode.REQUIRED)
    private AIPersonaDto aiPersona;

    @Schema(description = "대화 상태", requiredMode = Schema.RequiredMode.REQUIRED)
    private ConversationStatus status;

    @Schema(description = "대화 상황", requiredMode = Schema.RequiredMode.REQUIRED)
    private String situation;

    @Schema(description = "대화 생성일시", requiredMode = Schema.RequiredMode.REQUIRED, format = "date-time")
    private LocalDateTime createdAt;

    @Schema(description = "대화 종료일시", requiredMode = Schema.RequiredMode.NOT_REQUIRED, format = "date-time")
    private LocalDateTime endedAt;

    public static ConversationDto of(Conversation conversation, AIPersonaDto aiPersona) {
        return ConversationDto.builder()
                .conversationId(conversation.getId())
                .userId(conversation.getUserId())
                .aiPersona(aiPersona)
                .status(conversation.getStatus())
                .situation(conversation.getSituation())
                .createdAt(DateTimeUtil.toLocalDateTime(conversation.getCreatedAt()))
                .endedAt(DateTimeUtil.toLocalDateTime(conversation.getEndedAt()))
                .build();
    }
}