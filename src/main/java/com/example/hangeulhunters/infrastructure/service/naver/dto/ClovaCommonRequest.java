package com.example.hangeulhunters.infrastructure.service.naver.dto;

import com.example.hangeulhunters.application.conversation.dto.MessageDto;
import com.example.hangeulhunters.domain.conversation.constant.MessageType;
import com.example.hangeulhunters.domain.user.constant.KoreanLevel;
import com.example.hangeulhunters.infrastructure.service.naver.constant.PromptConstant;
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
public class ClovaCommonRequest {
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

        public static Message ofAIMessage(String aiMessage) {
            return Message.builder()
                    .role("assistant")
                    .content(List.of(
                            Content.builder()
                                    .type("text")
                                    .text(aiMessage)
                                    .build()
                    ))
                    .build();
        }

        public static Message ofUserMessage(String userMessage) {
            return Message.builder()
                    .role("user")
                    .content(List.of(
                            Content.builder()
                                    .type("text")
                                    .text(userMessage)
                                    .build()
                    ))
                    .build();
        }
    }

    public static ClovaCommonRequest ofGenerateReply(String aiRole, String userRole, String situation, KoreanLevel level, String userMessage) {
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
            messages.add(Message.ofUserMessage(userMessage));
        }

        return ClovaCommonRequest.builder()
                .messages(messages)
                .build();
    }

    public static ClovaCommonRequest ofFeedbackSentenceRequest(String aiRole, String userRole, String situation, String aiMessage, String userMessage) {
        return ClovaCommonRequest.builder()
                .messages(List.of(
                        Message.builder()
                                .role("system")
                                .content(List.of(
                                        Message.Content.builder()
                                                .type("text")
                                                .text(String.format(
                                                        PromptConstant.FEEDBACK_MESSAGE.getPromptMessage(),
                                                        aiRole,
                                                        userRole,
                                                        situation
                                                ))
                                                .build()
                                ))
                                .build(),
                        Message.ofAIMessage(aiMessage),
                        Message.ofUserMessage(userMessage)
                        )
                )
                .build();
    }

    public static ClovaCommonRequest ofFeedbackConversationRequest(String aiRole, String userRole, String situation, List<MessageDto> conversationMessages) {
        List<Message> messages = new ArrayList<>();
        messages.add(
                Message.builder()
                        .role("system")
                        .content(List.of(
                                Message.Content.builder()
                                        .type("text")
                                        .text(String.format(
                                                PromptConstant.FEEDBACK_MESSAGE.getPromptMessage(),
                                                aiRole,
                                                userRole,
                                                situation
                                        ))
                                        .build()
                ))
                .build()
        );

        conversationMessages.forEach(message -> {
            messages.add(
                    message.getType() == MessageType.AI
                            ? Message.ofAIMessage(message.getContent())
                            : Message.ofUserMessage(message.getContent())
            );
        });

        return ClovaCommonRequest.builder()
                .messages(messages)
                .build();
    }
}
