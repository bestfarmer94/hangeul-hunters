package com.example.hangeulhunters.domain.user.constant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Schema(description = "사용자 역할")
public enum UserRole {
    @Schema(description = "관리자")
    ROLE_ADMIN,
    
    @Schema(description = "일반 사용자")
    ROLE_USER,

    @Schema(description = "게스트 사용자")
    ROLE_GUEST
}