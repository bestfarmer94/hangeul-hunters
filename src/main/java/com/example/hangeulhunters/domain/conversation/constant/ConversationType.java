package com.example.hangeulhunters.domain.conversation.constant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;

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
    ASK(null, null, null, List.of(
            AskStep.builder()
                    .content("Who is this for?")
                    .approachTip("This can be something you’re about to say or do.")
                    .build(),
            AskStep.builder()
                    .content("How close are you to this person?")
                    .approachTip("Pick an option below or type your own.")
                    .build(),
            AskStep.builder()
                    .content("What kind of situation is this?")
                    .approachTip("You can briefly describe what’s going on.")
                    .build()
    )),

    /**
     * 롤플레잉 대화
     */
    @Schema(description = "롤플레잉")
    ROLE_PLAYING(null, null, null, Collections.emptyList()),

    /**
     * 면접 대화
     */
    @Schema(description = "면접")
    INTERVIEW(" 면접관", "interviewer", "applicant", Collections.emptyList()),;

    private final String aiNamePostFix;
    private final String aiRole;
    private final String userRole;
    private final List<AskStep> askSteps;

    @Getter
    @Builder
    public static class AskStep {
        private String content;
        private String approachTip;
    }
}
