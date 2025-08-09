package com.example.hangeulhunters.infrastructure.dto;

import com.example.hangeulhunters.domain.user.constant.KoreanLevel;
import com.example.hangeulhunters.infrastructure.constant.PromptConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenerateReplyRequest {
    private List<Message> messages;
    private Integer maxTokens;

    @Data
    @Builder
    public static class Message {
        private String role;
        private List<Content> content;

        @Data
        @Builder
        public static class Content {
            private String type;
            private String text;
        }
    }

    public static GenerateReplyRequest of(String aiRole, String userRole, String situation, KoreanLevel level, String userMessage) {
        List<Message> messages = new ArrayList<>();

        messages.add(
                Message.builder()
                        .role("system")
                        .content(List.of(
                                Message.Content.builder()
                                        .type("text")
                                        .text(String.format(
                                                PromptConstant.GENERATE_REPLY.getPromptMessage(),
                                                aiRole,
                                                userRole,
                                                situation,
                                                level)
                                        )
                                        .build()
                                )
                        )
                        .build()
        );

        if(userMessage != null) {
            messages.add(Message.builder()
                    .role("user")
                    .content(List.of(
                            Message.Content.builder()
                                    .type("text")
                                    .text(userMessage)
                                    .build()
                            )
                    )
                    .build());
        }

        return GenerateReplyRequest.builder()
                .messages(messages)
//                .maxTokens(20)
                .build();
    }
}
