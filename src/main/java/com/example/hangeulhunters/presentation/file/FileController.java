package com.example.hangeulhunters.presentation.file;

import com.example.hangeulhunters.application.file.dto.PresignedUrlRequest;
import com.example.hangeulhunters.application.file.service.FileService;
import com.example.hangeulhunters.infrastructure.service.aws.dto.PresignedUrlDto;
import com.example.hangeulhunters.presentation.common.ControllerSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 파일 컨트롤러
 */
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Tag(name = "File", description = "파일 업로드 API")
public class FileController extends ControllerSupport {

    private final FileService fileService;

    /**
     * Presigned URL 생성
     *
     * @param request Presigned URL 요청
     * @return Presigned URL 정보
     */
    @PostMapping("/presigned-url")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "Presigned URL 생성",
        description = "S3 업로드를 위한 Presigned URL을 생성합니다",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<PresignedUrlDto> generatePresignedUrl(@Valid @RequestBody PresignedUrlRequest request) {
        PresignedUrlDto presignedUrl = fileService.generatePresignedUrl(request);
        return ResponseEntity.ok(presignedUrl);
    }
}