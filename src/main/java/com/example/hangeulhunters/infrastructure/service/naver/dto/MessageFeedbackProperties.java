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
                        .description("더 나은 표현: 더 자연스럽고 적절한 한국어 표현 제공")
                        .build())
                .explain(Property.builder()
                        .type("string")
                        .description("문화적 설명: 왜 이 표현이 해당 상황에서 더 적합한지 영어로 설명")
                        .build())
                .build();
    }
}