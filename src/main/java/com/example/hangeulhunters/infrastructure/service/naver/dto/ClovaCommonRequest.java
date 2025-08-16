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

    public static ClovaCommonRequest ofGenerateReply(String aiRole, String userRole, String situation, KoreanLevel level, List<MessageDto> conversationMessages) {
        String systemPrompt = String.format(
                PromptConstant.GENERATE_REPLY.getPromptMessage(),
                aiRole, userRole, situation, level
        );
        
        return createWithSystemPromptAndMessages(systemPrompt, conversationMessages);
    }

    /**
     * 시스템 프롬프트와 대화 내역을 포함한 공통 요청 객체 생성
     *
     * @param systemPrompt 시스템 프롬프트 메시지
     * @param conversationMessages 대화 내역 (선택 사항)
     * @return ClovaCommonRequest 객체
     */
    private static ClovaCommonRequest createWithSystemPromptAndMessages(String systemPrompt, List<MessageDto> conversationMessages) {
        List<Message> messages = new ArrayList<>();

        // 시스템 프롬프트 추가
        messages.add(
                Message.builder()
                        .role("system")
                        .content(List.of(
                                Message.Content.builder()
                                        .type("text")
                                        .text(systemPrompt)
                                        .build()
                        ))
                        .build()
        );

        // 대화 내역 추가 (있는 경우)
        if (conversationMessages != null && !conversationMessages.isEmpty()) {
            conversationMessages.forEach(message -> {
                messages.add(
                        message.getType() == MessageType.AI
                                ? Message.ofAIMessage(message.getContent())
                                : Message.ofUserMessage(message.getContent())
                );
            });
        }

        return ClovaCommonRequest.builder()
                .messages(messages)
                .build();
    }
}
