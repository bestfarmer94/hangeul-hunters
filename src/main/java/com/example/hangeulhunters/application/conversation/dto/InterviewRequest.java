package com.example.hangeulhunters.application.conversation.dto;

import com.example.hangeulhunters.application.common.dto.FileRequest;
import com.example.hangeulhunters.domain.conversation.constant.InterviewStyle;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 면접 대화 생성 요청 DTO
 * 페르소나는 자동 생성됩니다.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "면접 대화 생성 요청")
public class InterviewRequest {

    @NotBlank(message = "회사명은 필수입니다")
    @Schema(description = "회사명", example = "네이버")
    private String companyName;

    @NotBlank(message = "직무는 필수입니다")
    @Schema(description = "직무", example = "백엔드 개발자")
    private String jobTitle;

    @Schema(description = "채용 공고", example = "Java/Spring 기반 백엔드 개발자를 모집합니다...")
    private String jobPosting;

    @Schema(description = "면접 스타일", example = "STANDARD")
    private InterviewStyle interviewStyle;

    @Schema(description = "파일 리스트")
    private List<FileRequest> files;
}
