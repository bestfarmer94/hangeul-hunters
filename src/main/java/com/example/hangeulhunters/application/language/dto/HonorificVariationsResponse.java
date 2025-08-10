package com.example.hangeulhunters.application.language.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class HonorificVariationsResponse {

    @Schema(description = "존댓말 레벨1", requiredMode = Schema.RequiredMode.REQUIRED)
    private String honorificLevel1;

    @Schema(description = "존댓말 레벨2", requiredMode = Schema.RequiredMode.REQUIRED)
    private String honorificLevel2;

    @Schema(description = "존댓말 레벨3", requiredMode = Schema.RequiredMode.REQUIRED)
    private String honorificLevel3;

    @Schema(description = "존댓말 레벨4", requiredMode = Schema.RequiredMode.REQUIRED)
    private String honorificLevel4;

    @Schema(description = "존댓말 레벨5", requiredMode = Schema.RequiredMode.REQUIRED)
    private String honorificLevel5;

    @Schema(description = "예시 및 설명", requiredMode = Schema.RequiredMode.REQUIRED)
    private String explain;
}