package com.example.hangeulhunters.application.common.dto;

import com.example.hangeulhunters.domain.common.constant.FileObjectType;
import com.example.hangeulhunters.domain.common.entity.File;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 파일 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "파일 정보")
public class FileDto {

    @Schema(description = "파일 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long fileId;

    @Schema(description = "객체 타입", requiredMode = Schema.RequiredMode.REQUIRED)
    private FileObjectType objectType;

    @Schema(description = "객체 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long objectId;

    @Schema(description = "파일 URL", requiredMode = Schema.RequiredMode.REQUIRED)
    private String fileUrl;

    @Schema(description = "파일명", requiredMode = Schema.RequiredMode.REQUIRED)
    private String fileName;

    @Schema(description = "파일 타입", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String fileType;

    @Schema(description = "파일 크기 (bytes)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private Long fileSize;

    public static FileDto fromEntity(File file) {
        return FileDto.builder()
                .fileId(file.getId())
                .objectType(file.getObjectType())
                .objectId(file.getObjectId())
                .fileUrl(file.getFileUrl())
                .fileName(file.getFileName())
                .fileType(file.getFileType())
                .fileSize(file.getFileSize())
                .build();
    }
}
