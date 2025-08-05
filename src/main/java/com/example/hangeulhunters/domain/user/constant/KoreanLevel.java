package com.example.hangeulhunters.domain.user.constant;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "한국어 능력 레벨")
public enum KoreanLevel {
    @Schema(description = "초급")
    BEGINNER,
    
    @Schema(description = "중급")
    INTERMEDIATE,
    
    @Schema(description = "고급")
    ADVANCED,
    
    @Schema(description = "원어민")
    NATIVE
}