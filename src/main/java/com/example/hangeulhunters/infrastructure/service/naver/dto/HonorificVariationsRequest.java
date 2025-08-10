package com.example.hangeulhunters.infrastructure.service.naver.dto;

import com.example.hangeulhunters.infrastructure.service.naver.constant.PromptConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HonorificVariationsRequest {
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
                private Property honorificLevel1;
                private Property honorificLevel2;
                private Property honorificLevel3;
                private Property honorificLevel4;
                private Property honorificLevel5;
                private Property explain;

                @Data
                @Builder
                public static class Property {
                    private String type;
                    private String description;
                }
            }
        }
    }

    public static HonorificVariationsRequest of(String aiRole, String sourceContent) {
        String explainMessage = aiRole != null
                ? String.format(PromptConstant.HONORIFIC_APPROPRIATE.getPromptMessage(), aiRole)
                : PromptConstant.HONORIFIC_EXAMPLE.getPromptMessage();

        return HonorificVariationsRequest.builder()
                .messages(List.of(
                        Message.builder()
                                .role("system")
                                .content(PromptConstant.HONORIFIC_VARIATIONS.getPromptMessage() + explainMessage)
                                .build(),
                        Message.builder()
                                .role("user")
                                .content(sourceContent)
                                .build()
                        )
                )
                .thinking(Thinking.builder()
                        .effort("none")
                        .build()
                )
                .responseFormat(ResponseFormat.builder()
                        .type("json")
                        .schema(ResponseFormat.Schema.builder()
                                .type("object")
                                .properties(ResponseFormat.Schema.Properties.builder()
                                        .honorificLevel1(ResponseFormat.Schema.Properties.Property.builder()
                                                .type("string")
                                                .description("Level 1: Intimate / Casual (해체)")
                                                .build()
                                        )
                                        .honorificLevel2(ResponseFormat.Schema.Properties.Property.builder()
                                                .type("string")
                                                .description("Level 2: Familiar (해라체)")
                                                .build()
                                        )
                                        .honorificLevel3(ResponseFormat.Schema.Properties.Property.builder()
                                                .type("string")
                                                .description("Level 3: Polite (해요체)")
                                                .build()
                                        )
                                        .honorificLevel4(ResponseFormat.Schema.Properties.Property.builder()
                                                .type("string")
                                                .description("Level 4: Formal (하십쇼체)")
                                                .build()
                                        )
                                        .honorificLevel5(ResponseFormat.Schema.Properties.Property.builder()
                                                .type("string")
                                                .description("Level 5: Royal (하소서체)")
                                                .build()
                                        )
                                        .explain(ResponseFormat.Schema.Properties.Property.builder()
                                                .type("string")
                                                .description(explainMessage)
                                                .build()
                                        )
                                        .build()
                                )
                                .required(List.of("honorificLevel1", "honorificLevel2", "honorificLevel3",
                                        "honorificLevel4", "honorificLevel5", "explain")
                                )
                                .build()
                        )
                        .build())
                .build();
    }
}
