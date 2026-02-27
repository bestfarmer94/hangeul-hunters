package com.example.hangeulhunters.application.conversation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "메시지 요청")
public class MessageRequest {
    @NotNull
    @Schema(description = "대화 ID")
    private Long conversationId;

    @Schema(description = "메시지")
    private String content;

    @Schema(description = "음성 파일의 Signed URL (STT용)", example = "https://storage.googleapis.com/noonchi-bucket/temp/audio.wav")
    private String audioUrl;
}