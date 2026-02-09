package com.example.hangeulhunters.application.conversation.dto;

import com.example.hangeulhunters.domain.conversation.entity.MessageFeedback;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 메시지 피드백 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageFeedbackDto {

    @Schema(description = "피드백 ID")
    private Long feedbackId;

    @Schema(description = "메시지 ID")
    private Long messageId;

    @Schema(description = "존댓말 점수 (0-100)")
    private Integer politenessScore;

    @Schema(description = "자연스러움 점수 (0-100)")
    private Integer naturalnessScore;

    @Schema(description = "발음 점수 (0-100)")
    private Integer pronunciationScore;

    @Schema(description = "적절한 표현 (optional)")
    private String appropriateExpression;

    @Schema(description = "문법/존댓말 피드백 (영어)")
    private String contentsFeedback;

    @Schema(description = "맥락/눈치 피드백 (영어)")
    private String nuanceFeedback;

    /**
     * 엔티티로부터 DTO 생성
     */
    public static MessageFeedbackDto fromEntity(MessageFeedback feedback) {
        return MessageFeedbackDto.builder()
                .feedbackId(feedback.getId())
                .messageId(feedback.getMessageId())
                .politenessScore(feedback.getPolitenessScore())
                .naturalnessScore(feedback.getNaturalnessScore())
                .pronunciationScore(feedback.getPronunciationScore())
                .appropriateExpression(feedback.getAppropriateExpression())
                .contentsFeedback(feedback.getContentsFeedback())
                .nuanceFeedback(feedback.getNuanceFeedback())
                .build();
    }
}