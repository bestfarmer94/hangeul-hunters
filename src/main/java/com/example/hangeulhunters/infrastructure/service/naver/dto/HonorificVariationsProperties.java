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
    private ExpressionsByFormalityProperty lowIntimacyExpressions;
    private ExpressionsByFormalityProperty mediumIntimacyExpressions;
    private ExpressionsByFormalityProperty highIntimacyExpressions;
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
     * @param explainDescription 설명 필드 설명
     * @return 존댓말 변형 속성
     */
    public static HonorificVariationsProperties createDefault(String explainDescription) {
        return HonorificVariationsProperties.builder()
                .lowIntimacyExpressions(ExpressionsByFormalityProperty.builder()
                        .lowFormality(StringProperty.builder()
                                .type("string")
                                .description("Low Intimacy / Low Formality Expression")
                                .build())
                        .mediumFormality(StringProperty.builder()
                                .type("string")
                                .description("Low Intimacy / Medium Formality Expression")
                                .build())
                        .highFormality(StringProperty.builder()
                                .type("string")
                                .description("Low Intimacy / High Formality Expression")
                                .build())
                        .required(List.of("lowFormality", "mediumFormality", "highFormality"))
                        .build())
                .mediumIntimacyExpressions(ExpressionsByFormalityProperty.builder()
                        .lowFormality(StringProperty.builder()
                                .type("string")
                                .description("Medium Intimacy / Low Formality Expression")
                                .build())
                        .mediumFormality(StringProperty.builder()
                                .type("string")
                                .description("Medium Intimacy / Medium Formality Expression")
                                .build())
                        .highFormality(StringProperty.builder()
                                .type("string")
                                .description("Medium Intimacy / High Formality Expression")
                                .build())
                        .required(List.of("lowFormality", "mediumFormality", "highFormality"))
                        .build())
                .highIntimacyExpressions(ExpressionsByFormalityProperty.builder()
                        .lowFormality(StringProperty.builder()
                                .type("string")
                                .description("High Intimacy / Low Formality Expression")
                                .build())
                        .mediumFormality(StringProperty.builder()
                                .type("string")
                                .description("High Intimacy / Medium Formality Expression")
                                .build())
                        .highFormality(StringProperty.builder()
                                .type("string")
                                .description("High Intimacy / High Formality Expression")
                                .build())
                        .required(List.of("lowFormality", "mediumFormality", "highFormality"))
                        .build())
                .explain(StringProperty.builder()
                        .type("string")
                        .description(explainDescription)
                        .build())
                .build();
    }
}