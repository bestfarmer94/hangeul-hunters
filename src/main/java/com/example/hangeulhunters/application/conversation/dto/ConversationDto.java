package com.example.hangeulhunters.application.conversation.dto;

import com.example.hangeulhunters.application.persona.dto.AIPersonaDto;
import com.example.hangeulhunters.domain.conversation.constant.ConversationStatus;
import com.example.hangeulhunters.domain.conversation.entity.Conversation;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationDto {

    @Schema(description = "대화 ID", required = true)
    private Long conversationId;

    @Schema(description = "사용자 ID (소유)", required = true)
    private Long userId;

    @Schema(description = "AI 정보", required = true)
    private AIPersonaDto aiPersona;

    @Schema(description = "대화 상태", required = true)
    private ConversationStatus status;

    @Schema(description = "대화 상황", required = true)
    private String situation;

    public static ConversationDto of(Conversation conversation, AIPersonaDto aiPersona) {
        return ConversationDto.builder()
                .conversationId(conversation.getId())
                .userId(conversation.getUserId())
                .aiPersona(aiPersona)
                .status(conversation.getStatus())
                .situation(conversation.getSituation())
                .build();
    }
}