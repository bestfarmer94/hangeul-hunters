package com.example.hangeulhunters.domain.conversation.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 개선점 항목 Value Object
 * AI 피드백의 개선점을 표현 (최대 3개)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImprovementItem {

    /**
     * 개선할 점 (예: "'팀장님이 말했어요' → '팀장님께서 말씀하셨어요'")
     */
    private String point;

    /**
     * 개선 팁 (예: "Use '께서 + -시-' when referring to superiors to show respect")
     */
    private String tip;
}
