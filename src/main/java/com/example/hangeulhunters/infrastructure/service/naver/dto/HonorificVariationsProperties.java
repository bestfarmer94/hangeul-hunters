package com.example.hangeulhunters.infrastructure.service.naver.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 존댓말 변형 응답을 위한 속성 클래스
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HonorificVariationsProperties {
    private ExpressionsByFormalityProperty closeIntimacyExpressions;
    private ExpressionsByFormalityProperty mediumIntimacyExpressions;
    private ExpressionsByFormalityProperty distantIntimacyExpressions;
    private StringProperty explain;

    @Data
    @Builder
    public static class ExpressionsByFormalityProperty {
        private StringProperty lowFormality;
        private StringProperty mediumFormality;
        private StringProperty highFormality;
        private List<String> required;
    }

    @Data
    @Builder
    public static class StringProperty {
        private String type;
        private String description;
    }

    /**
     * 기본 존댓말 변형 속성 생성
     * @return 존댓말 변형 속성
     */
    public static HonorificVariationsProperties createDefault() {
        return HonorificVariationsProperties.builder()
                .closeIntimacyExpressions(ExpressionsByFormalityProperty.builder()
                        .lowFormality(StringProperty.builder()
                                .type("string")
                                .description("Close-Low:** [반말로 변환 - \"했어\", \"갈게\", \"미안\"]")
                                .build())
                        .mediumFormality(StringProperty.builder()
                                .type("string")
                                .description("Close-Medium:** [편한 존댓말 - \"했어요\", \"갈게요\", \"미안해요\"]")
                                .build())
                        .highFormality(StringProperty.builder()
                                .type("string")
                                .description("Close-High:** [정중한 존댓말 - \"했습니다\", \"가겠습니다\", \"죄송해요\"]")
                                .build())
                        .required(List.of("lowFormality", "mediumFormality", "highFormality"))
                        .build())
                .mediumIntimacyExpressions(ExpressionsByFormalityProperty.builder()
                        .lowFormality(StringProperty.builder()
                                .type("string")
                                .description("Medium-Low:** [기본 존댓말 - \"했어요\", \"갈게요\", \"죄송해요\"]")
                                .build())
                        .mediumFormality(StringProperty.builder()
                                .type("string")
                                .description("Medium-Medium:** [표준 존댓말 - \"했습니다\", \"가겠습니다\", \"죄송합니다\"]")
                                .build())
                        .highFormality(StringProperty.builder()
                                .type("string")
                                .description("Medium-High:** [높은 존댓말 - \"했습니다\", \"가겠습니다\", \"죄송합니다\"]")
                                .build())
                        .required(List.of("lowFormality", "mediumFormality", "highFormality"))
                        .build())
                .distantIntimacyExpressions(ExpressionsByFormalityProperty.builder()
                        .lowFormality(StringProperty.builder()
                                .type("string")
                                .description("Distant-Low:** [정중한 존댓말 - \"했습니다\", \"가겠습니다\", \"죄송합니다\"]")
                                .build())
                        .mediumFormality(StringProperty.builder()
                                .type("string")
                                .description("Distant-Medium:** [매우 정중한 존댓말 - \"했습니다\", \"가겠습니다\", \"대단히 죄송합니다\"]")
                                .build())
                        .highFormality(StringProperty.builder()
                                .type("string")
                                .description("Distant-High:** [최고 격식 - \"했습니다\", \"가겠습니다\", \"깊이 사과드립니다\"]")
                                .build())
                        .required(List.of("lowFormality", "mediumFormality", "highFormality"))
                        .build())
                .explain(StringProperty.builder()
                        .type("string")
                        .description("영어로 간단 설명")
                        .build())
                .build();
    }
}