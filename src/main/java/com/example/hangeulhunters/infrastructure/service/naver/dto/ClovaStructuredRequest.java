package com.example.hangeulhunters.infrastructure.service.naver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * CLOVA Studio API의 구조화된 응답을 위한 공통 요청 클래스
 * @param <P> Properties 타입 파라미터 - 응답 형식의 속성 타입
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class ClovaStructuredRequest<P> {
    private List<Message> messages;
    private Thinking thinking;
    private ResponseFormat<P> responseFormat;

    @Data
    @Builder
    public static class Message {
        private String role;
        private String content;
    }

    @Data
    @Builder
    public static class Thinking {
        private String effort;
    }

    @Data
    @Builder
    public static class ResponseFormat<P> {
        private String type;
        private Schema<P> schema;

        @Data
        @Builder
        public static class Schema<P> {
            private String type;
            private P properties;
            private List<String> required;
        }
    }

    /**
     * 기본 구조화된 요청 객체 생성
     *
     * @param messages 메시지 목록
     * @param properties 응답 형식 속성
     * @param requiredFields 필수 필드 목록
     * @return 구조화된 요청 객체
     */
    protected <T extends ClovaStructuredRequest<P>> T createBaseRequest(
            List<Message> messages,
            P properties,
            List<String> requiredFields
    ) {
        // 새 인스턴스를 직접 생성하는 방식으로 변경
        this.messages = messages;
        this.thinking = Thinking.builder()
                .effort("none")
                .build();
        this.responseFormat = ResponseFormat.<P>builder()
                .type("json")
                .schema(ResponseFormat.Schema.<P>builder()
                        .type("object")
                        .properties(properties)
                        .required(requiredFields)
                        .build())
                .build();
        return (T) this;
    }
}