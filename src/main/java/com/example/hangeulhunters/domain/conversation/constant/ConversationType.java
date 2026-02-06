package com.example.hangeulhunters.domain.conversation.constant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 대화 타입 열거형
 */
@Getter
@RequiredArgsConstructor
@Schema(description = "대화 타입")
public enum ConversationType {
    /**
     * 질문
     */
    @Schema(description = "질문")
    ASK(null, null, null),

    /**
     * 롤플레잉 대화
     */
    @Schema(description = "롤플레잉")
    ROLE_PLAYING(null, null, null),

    /**
     * 면접 대화
     */
    @Schema(description = "면접")
    INTERVIEW(" 면접관", "interviewer", "applicant");

    private final String aiNamePostFix;
    private final String aiRole;
    private final String userRole;
}
