package com.example.hangeulhunters.application.conversation.dto;

import com.example.hangeulhunters.application.common.dto.FileDto;
import com.example.hangeulhunters.application.persona.dto.AIPersonaDto;
import com.example.hangeulhunters.domain.conversation.constant.ConversationStatus;
import com.example.hangeulhunters.domain.conversation.constant.ConversationType;
import com.example.hangeulhunters.domain.conversation.constant.InterviewStyle;
import com.example.hangeulhunters.domain.conversation.entity.Conversation;
import com.example.hangeulhunters.infrastructure.util.DateTimeUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationDto {

    @Schema(description = "대화 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long conversationId;

    @Schema(description = "사용자 ID (소유)", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

    @Schema(description = "AI 정보", requiredMode = Schema.RequiredMode.REQUIRED)
    private AIPersonaDto aiPersona;

    @Schema(description = "대화 타입", requiredMode = Schema.RequiredMode.REQUIRED)
    private ConversationType conversationType;

    @Schema(description = "대화 상태", requiredMode = Schema.RequiredMode.REQUIRED)
    private ConversationStatus status;

    @Schema(description = "대화 상황 (롤플레잉용)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String situation;

    @Schema(description = "CLOVA Studio Chat Task ID", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String chatModelId;

    @Schema(description = "대화 생성일시", requiredMode = Schema.RequiredMode.REQUIRED, format = "date-time")
    private LocalDateTime createdAt;

    @Schema(description = "대화 종료일시", requiredMode = Schema.RequiredMode.NOT_REQUIRED, format = "date-time")
    private LocalDateTime endedAt;

    @Schema(description = "마지막 활동 일시", requiredMode = Schema.RequiredMode.REQUIRED, format = "date-time")
    private LocalDateTime lastActivityAt;

    // Interview-specific fields
    @Schema(description = "회사명 (면접용)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String interviewCompanyName;

    @Schema(description = "직무 (면접용)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String interviewJobTitle;

    @Schema(description = "채용 공고 (면접용)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String interviewJobPosting;

    @Schema(description = "면접 스타일 (면접용)", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private InterviewStyle interviewStyle;

    @Schema(description = "파일 목록", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<FileDto> files;

    public static ConversationDto of(Conversation conversation, AIPersonaDto aiPersona, List<FileDto> files) {
        return ConversationDto.builder()
                .conversationId(conversation.getId())
                .userId(conversation.getUserId())
                .aiPersona(aiPersona)
                .conversationType(conversation.getConversationType())
                .status(conversation.getStatus())
                .situation(conversation.getSituation())
                .chatModelId(conversation.getChatModelId())
                .createdAt(DateTimeUtil.toLocalDateTime(conversation.getCreatedAt()))
                .endedAt(DateTimeUtil.toLocalDateTime(conversation.getEndedAt()))
                .lastActivityAt(DateTimeUtil.toLocalDateTime(conversation.getLastActivityAt()))
                .interviewCompanyName(conversation.getInterviewCompanyName())
                .interviewJobTitle(conversation.getInterviewJobTitle())
                .interviewJobPosting(conversation.getInterviewJobPosting())
                .interviewStyle(conversation.getInterviewStyle())
                .files(files)
                .build();
    }

    // Backward compatibility: method without files parameter
    public static ConversationDto of(Conversation conversation, AIPersonaDto aiPersona) {
        return of(conversation, aiPersona, List.of());
    }
}