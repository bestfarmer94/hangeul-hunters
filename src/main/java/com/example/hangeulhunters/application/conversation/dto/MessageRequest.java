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
public class MessageRequest {
    @NotNull
    @Schema(description = "대화 ID")
    private Long conversationId;

    @Schema(description = "메시지")
    private String content;

    @Schema(description = "음성 녹음 (오디오 파일의 Base64 인코딩 문자열)")
    private String audioBase64;
}