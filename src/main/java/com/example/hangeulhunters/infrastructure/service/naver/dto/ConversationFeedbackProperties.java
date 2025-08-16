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
                        .description("대화 내용 요약")
                        .build())
                .goodPoints(Property.builder()
                        .type("string")
                        .description("잘한 점")
                        .build())
                .improvementPoints(Property.builder()
                        .type("string")
                        .description("개선할 점")
                        .build())
                .improvementExamples(Property.builder()
                        .type("string")
                        .description("개선할 점에 대한 표현 예시")
                        .build())
                .overallEvaluation(Property.builder()
                        .type("string")
                        .description("한국어 능력 기준의 한줄평")
                        .build())
                .build();
    }
}