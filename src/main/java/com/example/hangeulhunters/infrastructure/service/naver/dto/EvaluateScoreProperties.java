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
                        .description("존댓말 점수")
                        .minimum(0)
                        .maximum(100)
                        .build())
                .naturalnessScore(Property.builder()
                        .type("number")
                        .description("자연스러움 점수")
                        .minimum(0)
                        .maximum(100)
                        .build())
                .build();
    }
}