package com.example.hangeulhunters.infrastructure.service.naver.dto;
import com.example.hangeulhunters.infrastructure.service.naver.constant.PromptConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Arrays;
import java.util.List;

/**
 * 메시지 피드백 요청을 위한 구조화된 요청 클래스
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class MessageFeedbackRequest extends ClovaStructuredRequest<MessageFeedbackProperties> {

    /**
     * 메시지 피드백 요청 객체 생성
     * @param aiRole AI 역할
     * @param userRole 사용자 역할
     * @param situation 상황
     * @param aiMessage AI 메시지
     * @param userMessage 사용자 메시지
     * @return 메시지 피드백 요청 객체
     */
    public static MessageFeedbackRequest of(String aiRole, String userRole, String situation, String aiMessage, String userMessage) {
        // 시스템 프롬프트 생성
        String systemPrompt = PromptConstant.NOONCHI_DEFINITION_PROMPT.getPromptMessage() +
                String.format(
                        PromptConstant.FEEDBACK_MESSAGE.getPromptMessage(),
                        aiRole, userRole, situation
                );

        // 메시지 목록 생성
        List<Message> messages = Arrays.asList(
                Message.builder()
                        .role("system")
                        .content(systemPrompt)
                        .build(),
                Message.builder()
                        .role("assistant")
                        .content(aiMessage)
                        .build(),
                Message.builder()
                        .role("user")
                        .content(userMessage)
                        .build()
        );

        // 필수 필드 목록
        List<String> requiredFields = Arrays.asList(
            "appropriateExpression", "explain"
        );

        // 속성 생성
        MessageFeedbackProperties properties = MessageFeedbackProperties.createDefault();

        // 요청 객체 생성 및 반환
        MessageFeedbackRequest request = new MessageFeedbackRequest();
        return request.createBaseRequest(messages, properties, requiredFields);
    }
}