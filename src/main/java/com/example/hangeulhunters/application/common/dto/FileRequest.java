package com.example.hangeulhunters.application.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@Schema(description = "파일 정보 생성 요청")
public class FileRequest {

    @NotBlank
    @Schema(description = "파일 URL")
    private String fileUrl;

    @NotBlank
    @Schema(description = "파일명")
    private String fileName;

    @Schema(description = "파일 타입")
    private String fileType;

    @Schema(description = "파일 크기 (bytes)")
    private Long fileSize;
}
