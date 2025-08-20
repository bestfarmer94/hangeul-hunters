package com.example.hangeulhunters.application.language.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * TTS API 요청 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TTSRequest {

    @NotBlank(message = "텍스트를 입력해주세요.")
    @Size(max = 1000, message = "텍스트는 최대 1000자까지 입력 가능합니다.")
    @Schema(description = "변환할 텍스트", example = "안녕하세요. 반갑습니다.")
    private String text;

    @Schema(description = "목소리", example = "ko-KR-Chirp3-HD-Leda")
    private String voice;
}