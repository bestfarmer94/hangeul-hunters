package com.example.hangeulhunters.infrastructure.dto;

import com.example.hangeulhunters.infrastructure.constant.PromptConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluateRequest {
    private List<Message> messages;
    private Thinking thinking;
    private ResponseFormat responseFormat;

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
    public static class ResponseFormat {
        private String type;
        private Schema schema;

        @Data
        @Builder
        public static class Schema {
            private String type;
            private Properties properties;
            private List<String> required;

            @Data
            @Builder
            public static class Properties {
                private Property politenessScore;
                private Property naturalnessScore;

                @Data
                @Builder
                public static class Property {
                    private String type;
                    private String description;
                    private Integer minimum;
                    private Integer maximum;
                }
            }
        }
    }

    public static EvaluateRequest of(String aiRole, String userRole, String situation, String aiMessage, String userMessage) {
        return EvaluateRequest.builder()
                .messages(List.of(
                        Message.builder()
                                .role("system")
                                .content(String.format(PromptConstant.EVALUATE_MESSAGE.getPromptMessage(), aiRole, userRole, situation))
                                .build(),
                        Message.builder()
                                .role("assistant")
                                .content(aiMessage)
                                .build(),
                        Message.builder()
                                .role("user")
                                .content(userMessage)
                                .build()
                ))
                .thinking(Thinking.builder()
                        .effort("none")
                        .build()
                )
                .responseFormat(ResponseFormat.builder()
                        .type("json")
                        .schema(ResponseFormat.Schema.builder()
                                .type("object")
                                .properties(ResponseFormat.Schema.Properties.builder()
                                        .politenessScore(ResponseFormat.Schema.Properties.Property.builder()
                                                .type("number")
                                                .description("존댓말 점수")
                                                .minimum(0)
                                                .maximum(100)
                                                .build())
                                        .naturalnessScore(ResponseFormat.Schema.Properties.Property.builder()
                                                .type("number")
                                                .description("자연스러움 점수")
                                                .minimum(0)
                                                .maximum(100)
                                                .build())
                                        .build())
                                .required(List.of("politenessScore", "naturalnessScore"))
                                .build())
                        .build())
                .build();
    }
}
