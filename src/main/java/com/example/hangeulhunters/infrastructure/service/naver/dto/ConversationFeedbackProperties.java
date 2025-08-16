package com.example.hangeulhunters.infrastructure.service.naver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 대화 피드백 응답을 위한 속성 클래스
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationFeedbackProperties {
    private Property summary;
    private Property goodPoints;
    private Property improvementPoints;
    private Property improvementExamples;
    private Property overallEvaluation;

    @Data
    @Builder
    public static class Property {
        private String type;
        private String description;
    }

    /**
     * 기본 대화 피드백 속성 생성
     * @return 대화 피드백 속성
     */
    public static ConversationFeedbackProperties createDefault() {
        return ConversationFeedbackProperties.builder()
                .summary(Property.builder()
                        .type("string")
                        .description("Summary in English: Briefly summarize the main points of the conversation.")
                        .build())
                .goodPoints(Property.builder()
                        .type("string")
                        .description("Explain What worked well in English: What the user did well in the conversation.")
                        .build())
                .improvementPoints(Property.builder()
                        .type("string")
                        .description("Explain What needs improvement in English: What the user needs to improve.")
                        .build())
                .improvementExamples(Property.builder()
                        .type("string")
                        .description("Examples of expressions for improvement: Specific examples of expressions that need improvement.")
                        .build())
                .overallEvaluation(Property.builder()
                        .type("string")
                        .description("One-line evaluation based on Korean proficiency in English: An overall evaluation of the user's Korean proficiency.")
                        .build())
                .build();
    }
}