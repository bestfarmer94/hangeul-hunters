package com.example.hangeulhunters.domain.conversation.constant;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 면접 스타일 열거형
 */
@Schema(description = "면접 스타일")
public enum InterviewStyle {
    /**
     * 친근한 면접
     */
    @Schema(description = "친근한 면접")
    FRIENDLY,

    /**
     * 표준 면접
     */
    @Schema(description = "표준 면접")
    STANDARD,

    /**
     * 엄격한 면접
     */
    @Schema(description = "엄격한 면접")
    STRICT
}
