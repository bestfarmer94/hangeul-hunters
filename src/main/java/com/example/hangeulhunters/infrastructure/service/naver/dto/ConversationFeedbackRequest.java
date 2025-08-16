package com.example.hangeulhunters.infrastructure.service.naver.dto;

import com.example.hangeulhunters.application.conversation.dto.MessageDto;
import com.example.hangeulhunters.domain.conversation.constant.MessageType;
import com.example.hangeulhunters.infrastructure.service.naver.constant.PromptConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 대화 피드백 요청을 위한 구조화된 요청 클래스
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class ConversationFeedbackRequest extends ClovaStructuredRequest<ConversationFeedbackProperties> {

    /**
     * 대화 피드백 요청 객체 생성
     * @param aiRole AI 역할
     * @param userRole 사용자 역할
     * @param situation 상황
     * @param messages 대화 메시지 목록
     * @return 대화 피드백 요청 객체
     */
    public static ConversationFeedbackRequest of(String aiRole, String userRole, String situation, List<MessageDto> messages) {
        // 시스템 프롬프트 생성
        String systemPrompt = String.format(
                PromptConstant.FEEDBACK_CONVERSATION.getPromptMessage(),
                aiRole, userRole, situation
        );

        // 메시지 목록 생성
        List<Message> messageList = new ArrayList<>();
        
        // 시스템 메시지 추가
        messageList.add(Message.builder()
                .role("system")
                .content(systemPrompt)
                .build());
        
        // 대화 메시지 추가
        for (MessageDto message : messages) {
            String role = message.getType() == MessageType.AI ? "assistant" : "user";
            messageList.add(Message.builder()
                    .role(role)
                    .content(message.getContent())
                    .build());
        }

        // 필수 필드 목록
        List<String> requiredFields = Arrays.asList(
                "summary", 
                "goodPoints", 
                "improvementPoints", 
                "improvementExamples", 
                "overallEvaluation"
        );

        // 속성 생성
        ConversationFeedbackProperties properties = ConversationFeedbackProperties.createDefault();

        // 요청 객체 생성 및 반환
        ConversationFeedbackRequest request = new ConversationFeedbackRequest();
        return request.createBaseRequest(messageList, properties, requiredFields);
    }
}