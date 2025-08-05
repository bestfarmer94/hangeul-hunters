package com.example.hangeulhunters.domain.user.constant;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "인증 제공자")
public enum AuthProvider {
    @Schema(description = "구글")
    GOOGLE,
    @Schema(description = "애플")
    APPLE
}