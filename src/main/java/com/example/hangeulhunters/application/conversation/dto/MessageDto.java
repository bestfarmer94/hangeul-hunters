package com.example.hangeulhunters.application.conversation.dto;

import com.example.hangeulhunters.domain.conversation.constant.MessageType;
import com.example.hangeulhunters.domain.conversation.entity.Message;
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
public class MessageDto {

    @Schema(description = "메시지 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long messageId;

    @Schema(description = "대화 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long conversationId;

    @Schema(description = "메시지 타입", requiredMode = Schema.RequiredMode.REQUIRED)
    private MessageType type;

    @Schema(description = "메시지 내용", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @Schema(description = "번역된 메시지 내용", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String translatedContent;

    @Schema(description = "오디오 URL", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String audioUrl;

    @Schema(description = "존댓말 점수", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer politenessScore;

    @Schema(description = "맥락 점수", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer naturalnessScore;

    @Schema(description = "발음 점수", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer pronunciationScore;

    // AI Response fields
    @Schema(description = "AI 반응 이모지", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String reactionEmoji;

    @Schema(description = "AI 반응 설명", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String reactionDescription;

    @Schema(description = "AI 반응 이유 (영어)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String reactionReason;

    @Schema(description = "추천 응답 (한국어)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String recommendation;

    @Schema(description = "메시지 생성일시", requiredMode = Schema.RequiredMode.REQUIRED, format = "date-time")
    private LocalDateTime createdAt;

    public static MessageDto fromEntity(Message message) {
        return MessageDto.builder()
                .messageId(message.getId())
                .conversationId(message.getConversationId())
                .type(message.getType())
                .content(message.getContent())
                .translatedContent(message.getTranslatedContent())
                .audioUrl(message.getAudioUrl())
                .politenessScore(message.getPolitenessScore())
                .naturalnessScore(message.getNaturalnessScore())
                .pronunciationScore(message.getPronunciationScore())
                .reactionEmoji(message.getReactionEmoji())
                .reactionDescription(message.getReactionDescription())
                .reactionReason(message.getReactionReason())
                .recommendation(message.getRecommendation())
                .createdAt(DateTimeUtil.toLocalDateTime(message.getCreatedAt()))
                .build();
    }
}