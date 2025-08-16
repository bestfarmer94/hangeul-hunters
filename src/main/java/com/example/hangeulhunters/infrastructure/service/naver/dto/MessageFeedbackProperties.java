package com.example.hangeulhunters.infrastructure.service.naver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 메시지 피드백 응답을 위한 속성 클래스
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageFeedbackProperties {
    private Property appropriateExpression;
    private Property explain;

    @Data
    @Builder
    public static class Property {
        private String type;
        private String description;
    }

    /**
     * 기본 메시지 피드백 속성 생성
     * @return 메시지 피드백 속성
     */
    public static MessageFeedbackProperties createDefault() {
        return MessageFeedbackProperties.builder()
                .appropriateExpression(Property.builder()
                        .type("string")
                        .description("Appropriate expressions that fit the conversation flow: Suggestions for expressions that can make the user's response more natural.")
                        .build())
                .explain(Property.builder()
                        .type("string")
                        .description("Explanation of the expression in English: An explanation of why the suggested expression is more appropriate.")
                        .build())
                .build();
    }
}