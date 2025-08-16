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
                        .description("대화 흐름에 맞는 적절한 표현")
                        .build())
                .explain(Property.builder()
                        .type("string")
                        .description("표현에 대한 설명")
                        .build())
                .build();
    }
}