package com.example.hangeulhunters.application.conversation.dto;

import com.example.hangeulhunters.domain.conversation.constant.ConversationSortBy;
import com.example.hangeulhunters.domain.conversation.constant.ConversationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 대화 필터링 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "대화 필터링 요청 DTO")
public class ConversationFilterRequest {
    
    @Schema(description = "대화 상태 (ACTIVE: 진행 중, ENDED: 종료됨)", example = "ACTIVE")
    private ConversationStatus status;
    
    @Schema(description = "AI 페르소나 ID", example = "1")
    private Long personaId;

    @Schema(description = "정렬 기준", example = "CREATED_AT")
    private ConversationSortBy sortBy;

    @Schema(description = "페이지 번호 (1부터 시작)", example = "1")
    private Integer page;

    @Schema(description = "페이지 크기", example = "10")
    private Integer size;
}