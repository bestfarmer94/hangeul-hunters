package com.example.hangeulhunters.application.conversation.dto;

import com.example.hangeulhunters.domain.conversation.entity.ConversationFeedback;
import com.example.hangeulhunters.domain.conversation.vo.ImprovementItem;
import com.example.hangeulhunters.domain.conversation.vo.KeyExpression;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 대화 전체 피드백 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationFeedbackDto {

    @Schema(description = "피드백 ID")
    private Long feedbackId;

    @Schema(description = "대화 ID")
    private Long conversationId;

    @Schema(description = "존댓말 점수 (0-100)")
    private Integer politenessScore;

    @Schema(description = "자연스러움 점수 (0-100)")
    private Integer naturalnessScore;

    @Schema(description = "발음 점수 (0-100)")
    private Integer pronunciationScore;

    @Schema(description = "대화 내용 요약")
    private String summary;

    @Schema(description = "잘한 점")
    private String goodPoints;

    @Schema(description = "개선할 점")
    private List<ImprovementItem> improvementPoints;

    @Schema(description = "한국어 능력 기준의 한줄평")
    private String overallEvaluation;

    /**
     * 엔티티로부터 DTO 생성
     */
    public static ConversationFeedbackDto fromEntity(ConversationFeedback feedback) {
        return ConversationFeedbackDto.builder()
                .feedbackId(feedback.getId())
                .conversationId(feedback.getConversationId())
                .politenessScore(feedback.getPolitenessScore())
                .naturalnessScore(feedback.getNaturalnessScore())
                .pronunciationScore(feedback.getPronunciationScore())
                .summary(feedback.getSummary())
                .goodPoints(feedback.getGoodPoints())
                .improvementPoints(feedback.getImprovementPoints())
                .overallEvaluation(feedback.getOverallEvaluation())
                .build();
    }
}