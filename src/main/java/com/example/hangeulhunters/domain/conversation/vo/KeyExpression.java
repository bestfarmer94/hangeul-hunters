package com.example.hangeulhunters.domain.conversation.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 핵심 표현 Value Object
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeyExpression {

    /**
     * 핵심 표현 (한국어)
     */
    private String korean;

    /**
     * 핵심 표현 (영어)
     */
    private String english;

    /**
     * 사용 예시
     */
    private String usage;
}
