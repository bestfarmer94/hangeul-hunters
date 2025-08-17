package com.example.hangeulhunters.infrastructure.service.naver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 평가 점수 응답을 위한 속성 클래스
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EvaluateScoreProperties {
    private Property politenessScore;
    private Property naturalnessScore;

    @Data
    @Builder
    public static class Property {
        private String type;
        private String description;
        private Integer minimum;
        private Integer maximum;
    }

    /**
     * 기본 평가 점수 속성 생성
     * @return 평가 점수 속성
     */
    public static EvaluateScoreProperties createDefault() {
        return EvaluateScoreProperties.builder()
                .politenessScore(Property.builder()
                        .type("number")
                        .description("정중함 점수 (0-100): 해당 관계와 상황에서 얼마나 적절한 존댓말 수준을 사용했는가?")
                        .minimum(0)
                        .maximum(100)
                        .build())
                .naturalnessScore(Property.builder()
                        .type("number")
                        .description("자연스러움 점수 (0-100): 실제 한국인이 사용할 법한 자연스러운 표현인가?")
                        .minimum(0)
                        .maximum(100)
                        .build())
                .build();
    }
}