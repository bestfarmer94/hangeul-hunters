package com.example.hangeulhunters.application.language.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "STT 변환 요청")
public class SpeechToTextRequest {
    @NotBlank
    @Schema(description = "음성 파일의 Presigned URL", example = "https://hangeulhunters.s3.ap-northeast-2.amazonaws.com/temp/audio.wav?...")
    private String audioUrl;
}
