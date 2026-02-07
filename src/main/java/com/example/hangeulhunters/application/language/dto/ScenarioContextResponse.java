package com.example.hangeulhunters.application.language.dto;

import com.example.hangeulhunters.infrastructure.service.noonchi.dto.NoonchiAiDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ScenarioContextResponse {
    @Schema(description = "사용자 역할", example = "직장 상사", requiredMode = Schema.RequiredMode.REQUIRED)
    private String myRole;

    @Schema(description = "AI 역할", example = "직장 부하 직원", requiredMode = Schema.RequiredMode.REQUIRED)
    private String aiRole;

    @Schema(description = "상세 내용", example = "이번 프로젝트의 진행 상황에 대해 논의합니다.", requiredMode =  Schema.RequiredMode.REQUIRED)
    private String detail;

    public static ScenarioContextResponse of(NoonchiAiDto.ScenarioContextAiResponse aiResponse) {
        return ScenarioContextResponse.builder()
                .myRole(aiResponse.getMyRole())
                .aiRole(aiResponse.getAiRole())
                .detail(aiResponse.getDetail())
                .build();
    }
}
