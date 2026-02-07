package com.example.hangeulhunters.application.language.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ScenarioContextRequest {
    @NotNull
    @Schema(description = "시나리오 ID", example = "1")
    private Long scenarioId;

    @Schema(description = "사용자 역할", example = "직장 상사")
    private String myRole;

    @Schema(description = "AI 역할", example = "직장 부하 직원")
    private String aiRole;

    @Schema(description = "상세 내용", example = "이번 프로젝트의 진행 상황에 대해 논의합니다.")
    private String detail;
}
