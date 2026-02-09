package com.example.hangeulhunters.application.conversation.dto;

import com.example.hangeulhunters.domain.conversation.entity.ConversationFeedback;
import com.example.hangeulhunters.domain.conversation.vo.ImprovementItem;
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

    @Schema(description = "피드백 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long feedbackId;

    @Schema(description = "대화 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long conversationId;

    @Schema(description = "존댓말 점수 (0-100)", example = "85", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer politenessScore;

    @Schema(description = "자연스러움 점수 (0-100)", example = "90", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer naturalnessScore;

    @Schema(description = "발음 점수 (0-100)", example = "80", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Integer pronunciationScore;

    @Schema(description = "대화 내용 요약", example = "이번 대화에서는 직장 내에서 업무에 대해 질문하는 상황을 다루었습니다.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String summary;

    @Schema(description = "잘한 점", example = "적절한 존댓말 사용과 자연스러운 표현이 돋보였습니다.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String goodPoints;

    @Schema(description = "개선할 점", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<ImprovementItem> improvementPoints;

    @Schema(description = "한국어 능력 기준의 한줄평", example = "전반적으로 훌륭한 대화였으나, 몇 가지 발음 개선이 필요합니다.", requiredMode = Schema.RequiredMode.REQUIRED)
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