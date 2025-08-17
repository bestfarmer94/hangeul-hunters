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
                        .description("대화 상황과 주요 내용을 2-3문장으로 간결하게 영어로 요약")
                        .build())
                .goodPoints(Property.builder()
                        .type("string")
                        .description("사용자의 긍정적 측면을 2-3문장으로 구체적으로 영어로 서술")
                        .build())
                .improvementPoints(Property.builder()
                        .type("string")
                        .description("가장 중요한 개선점을 2-3문장으로 영어로 설명")
                        .build())
                .improvementExamples(Property.builder()
                        .type("string")
                        .description("개선된 한국어 표현")
                        .build())
                .overallEvaluation(Property.builder()
                        .type("string")
                        .description("전반적인 대화 수행에 대한 간단한 영어로 된 평가 메시지 (1-2문장)")
                        .build())
                .build();
    }
}