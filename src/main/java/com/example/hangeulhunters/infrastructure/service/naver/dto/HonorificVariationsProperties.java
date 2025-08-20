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
                                .description("친밀도[Close] - 가족/친구 (편한 사이) + 형식도[Low] - 편안한 곳 (집/카페) 표현")
                                .build())
                        .mediumFormality(StringProperty.builder()
                                .type("string")
                                .description("친밀도[Close] - 가족/친구 (편한 사이) + 형식도[Medium] - 회사/학교 (중간 격식) 표현")
                                .build())
                        .highFormality(StringProperty.builder()
                                .type("string")
                                .description("친밀도[Close] - 가족/친구 (편한 사이) + 형식도[High] - 정부기관/계약 (극존칭) 표현")
                                .build())
                        .required(List.of("lowFormality", "mediumFormality", "highFormality"))
                        .build())
                .mediumIntimacyExpressions(ExpressionsByFormalityProperty.builder()
                        .lowFormality(StringProperty.builder()
                                .type("string")
                                .description("친밀도[Medium] - 동료/지인 (기본 존중) + 형식도[Low] - 편안한 곳 (집/카페) 표현")
                                .build())
                        .mediumFormality(StringProperty.builder()
                                .type("string")
                                .description("친밀도[Medium] - 동료/지인 (기본 존중) + 형식도[Medium] - 회사/학교 (중간 격식) 표현")
                                .build())
                        .highFormality(StringProperty.builder()
                                .type("string")
                                .description("친밀도[Medium] - 동료/지인 (기본 존중) + 형식도[High] - 정부기관/계약 (극존칭) 표현")
                                .build())
                        .required(List.of("lowFormality", "mediumFormality", "highFormality"))
                        .build())
                .distantIntimacyExpressions(ExpressionsByFormalityProperty.builder()
                        .lowFormality(StringProperty.builder()
                                .type("string")
                                .description("친밀도[Distant] - 처음 만난 분/고객 (최대 예의) + 형식도[Low] - 편안한 곳 (집/카페) 표현")
                                .build())
                        .mediumFormality(StringProperty.builder()
                                .type("string")
                                .description("친밀도[Distant] - 처음 만난 분/고객 (최대 예의) + 형식도[Medium] - 회사/학교 (중간 격식) 표현")
                                .build())
                        .highFormality(StringProperty.builder()
                                .type("string")
                                .description("친밀도[Distant] - 처음 만난 분/고객 (최대 예의) + 형식도[High] - 정부기관/계약 (극존칭) 표현")
                                .build())
                        .required(List.of("lowFormality", "mediumFormality", "highFormality"))
                        .build())
                .explain(StringProperty.builder()
                        .type("string")
                        .description("가장 실용적인 3-4개 표현을 선별하여 영어로 설명")
                        .build())
                .build();
    }
}