package com.example.hangeulhunters.infrastructure.service.naver.dto;

import com.example.hangeulhunters.infrastructure.service.naver.constant.PromptConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Arrays;
import java.util.List;

/**
 * 평가 점수 요청을 위한 구조화된 요청 클래스
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class EvaluateScoreRequest extends ClovaStructuredRequest<EvaluateScoreProperties> {

    /**
     * 평가 점수 요청 객체 생성
     * @param aiRole AI 역할
     * @param userRole 사용자 역할
     * @param situation 상황
     * @param aiMessage AI 메시지
     * @param userMessage 사용자 메시지
     * @return 평가 점수 요청 객체
     */
    public static EvaluateScoreRequest of(String aiRole, String userRole, String situation, String aiMessage, String userMessage) {
        // 시스템 프롬프트 생성
        String systemPrompt = PromptConstant.NOONCHI_DEFINITION_PROMPT.getPromptMessage() +
                String.format(
                        PromptConstant.EVALUATE_SCORE.getPromptMessage(),
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
        List<String> requiredFields = Arrays.asList("politenessScore", "naturalnessScore");

        // 속성 생성
        EvaluateScoreProperties properties = EvaluateScoreProperties.createDefault();

        // 요청 객체 생성 및 반환
        EvaluateScoreRequest request = new EvaluateScoreRequest();
        return request.createBaseRequest(messages, properties, requiredFields);
    }
}