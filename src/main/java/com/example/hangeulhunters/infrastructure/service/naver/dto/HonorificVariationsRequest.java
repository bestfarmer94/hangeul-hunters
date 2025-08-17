package com.example.hangeulhunters.infrastructure.service.naver.dto;

import com.example.hangeulhunters.infrastructure.service.naver.constant.PromptConstant;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Arrays;
import java.util.List;

/**
 * 존댓말 변형 요청을 위한 구조화된 요청 클래스
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
public class HonorificVariationsRequest extends ClovaStructuredRequest<HonorificVariationsProperties> {

    /**
     * 존댓말 변형 요청 객체 생성
     * @param sourceContent 원본 내용
     * @return 존댓말 변형 요청 객체
     */
    public static HonorificVariationsRequest of(String sourceContent) {
        // 시스템 프롬프트 생성
        String systemPrompt = PromptConstant.HONORIFIC_SLIDER.getPromptMessage();

        // 메시지 목록 생성
        List<Message> messages = Arrays.asList(
                Message.builder()
                        .role("system")
                        .content(systemPrompt)
                        .build(),
                Message.builder()
                        .role("user")
                        .content(sourceContent)
                        .build()
        );

        // 필수 필드 목록
        List<String> requiredFields = Arrays.asList(
                "lowIntimacyExpressions", "mediumIntimacyExpressions", "highIntimacyExpressions", "explain"
        );

        // 속성 생성
        HonorificVariationsProperties properties = HonorificVariationsProperties.createDefault();

        // 요청 객체 생성 및 반환
        HonorificVariationsRequest request = new HonorificVariationsRequest();
        return request.createBaseRequest(messages, properties, requiredFields);
    }
}