package com.example.hangeulhunters.infrastructure.dto;

import com.example.hangeulhunters.infrastructure.constant.PromptConstant;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class GenerateReplyRequest {
    private List<Message> messages;

    @Builder
    public static class Message {
        private String role;
        private Content content;

        @Builder
        public static class Content {
            private String type;
            private String text;
        }
    }

    public static GenerateReplyRequest of(String situation, String aiMessage, String message) {
        return GenerateReplyRequest.builder()
                .messages(List.of(
                        Message.builder()
                                .role("system")
                                .content(Message.Content.builder()
                                        .type("text")
                                        .text(situation + PromptConstant.GENERATE_REPLY.getPromptMessage())
                                        .build())
                                .build(),
                        Message.builder()
                                .role("assistant")
                                .content(Message.Content.builder()
                                        .type("text")
                                        .text(aiMessage)
                                        .build())
                                .build(),
                        Message.builder()
                                .role("user")
                                .content(Message.Content.builder()
                                        .type("text")
                                        .text(message)
                                        .build())
                                .build()
                ))
                .build();
    }
}
