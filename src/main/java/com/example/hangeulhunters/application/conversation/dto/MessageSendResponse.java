package com.example.hangeulhunters.application.conversation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageSendResponse {

    @Schema(description = "task 결과 정보", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private TaskResponse taskResult;

    @Schema(description = "메시지 정보 (AI, USER)", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<MessageDto> messages;

    @Data
    @Builder
    public static class TaskResponse {

        @Schema(description = "현재 task 완료 여부", requiredMode = Schema.RequiredMode.REQUIRED)
        private Boolean isTaskCompleted;

        @Schema(description = "모든 task 완료 여부", requiredMode = Schema.RequiredMode.REQUIRED)
        private Boolean isTaskAllCompleted;

        @Schema(description = "다음 task 레벨 (task 완료 여부 반영 이후)", requiredMode = Schema.RequiredMode.REQUIRED)
        private Integer resultTaskLevel;

        @Schema(description = "다음 task 내용 (task 완료 여부 반영 이후)", requiredMode = Schema.RequiredMode.REQUIRED)
        private String resultTaskName;
    }

    public static MessageSendResponse of(Boolean isTaskCompleted, ConversationDto conversation, List<MessageDto> messages) {
        return MessageSendResponse.builder()
                .taskResult(TaskResponse.builder()
                        .isTaskCompleted(isTaskCompleted)
                        .isTaskAllCompleted(conversation.getTaskAllCompleted())
                        .resultTaskLevel(conversation.getTaskCurrentLevel())
                        .resultTaskName(conversation.getTaskCurrentName())
                        .build())
                .messages(messages)
                .build();
    }
}
