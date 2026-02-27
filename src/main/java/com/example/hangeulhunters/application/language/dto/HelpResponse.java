package com.example.hangeulhunters.application.language.dto;

import com.example.hangeulhunters.infrastructure.service.noonchi.dto.NoonchiAiDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class HelpResponse {
    @Schema(description = "한국어 응답 추천 리스트", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> suggestions;

    @Schema(description = "각 추천에 대한 설명 리스트", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> explanations;

    @Schema(description = "각 추천의 영어 번역 리스트", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<String> translations;

    @Schema(description = "잘못된 응답의 인덱스 (0부터 시작)", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer wrongIndex;

    public static HelpResponse of(NoonchiAiDto.HelpAiResponse aiResponse) {
        return HelpResponse.builder()
                .suggestions(aiResponse.getSuggestions())
                .explanations(aiResponse.getExplanations())
                .translations(aiResponse.getTranslations())
                .wrongIndex(aiResponse.getWrongIndex())
                .build();
    }
}
