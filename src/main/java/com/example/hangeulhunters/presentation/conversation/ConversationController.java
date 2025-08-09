package com.example.hangeulhunters.presentation.conversation;

import com.example.hangeulhunters.application.common.dto.PageResponse;
import com.example.hangeulhunters.application.conversation.dto.ConversationDto;
import com.example.hangeulhunters.application.conversation.dto.ConversationFilterRequest;
import com.example.hangeulhunters.application.conversation.dto.ConversationRequest;
import com.example.hangeulhunters.application.conversation.service.ConversationService;
import com.example.hangeulhunters.domain.conversation.constant.ConversationSortBy;
import com.example.hangeulhunters.domain.conversation.constant.ConversationStatus;
import com.example.hangeulhunters.presentation.common.ControllerSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
@Tag(name = "Conversation", description = "Conversation API")
public class ConversationController extends ControllerSupport {

    private final ConversationService conversationService;

    @GetMapping("/{conversationId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "대화방 상세 조회",
            description = "특정 대화방의 정보를 조회합니다",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ConversationDto> getConversationById(
            @Parameter(description = "대화방 ID") @PathVariable Long conversationId) {

        ConversationDto conversation = conversationService.getConversationById(getCurrentUserId(), conversationId);
        return ResponseEntity.ok(conversation);
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "현재 사용자의 대화 목록 조회",
        description = "로그인한 사용자의 대화 목록을 필터링하여 페이징 조회합니다",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<PageResponse<ConversationDto>> getUserConversations(
            @Parameter(description = "대화 상태") @RequestParam(required = false) ConversationStatus status,
            @Parameter(description = "AI 페르소나 ID") @RequestParam(required = false) Long personaId,
            @Parameter(description = "정렬 기준") @RequestParam(defaultValue = "CREATED_AT_DESC") ConversationSortBy sortBy,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") Integer size) {
        
        PageResponse<ConversationDto> conversations = 
                conversationService.getUserConversations(
                        getCurrentUserId(),
                        ConversationFilterRequest.builder()
                                .status(status)
                                .personaId(personaId)
                                .sortBy(sortBy)
                                .page(page)
                                .size(size)
                                .build()
                );
        return ResponseEntity.ok(conversations);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "새 대화 생성",
        description = "AI 페르소나와의 새로운 대화를 시작합니다",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<ConversationDto> createConversation(@Valid @RequestBody ConversationRequest request) {
        ConversationDto conversation = conversationService.createConversation(getCurrentUserId(), request);
        return ResponseEntity.ok(conversation);
    }

    @PutMapping("/{conversationId}/end")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "대화 종료",
        description = "진행 중인 대화를 종료합니다",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<Void> endConversation(@PathVariable Long conversationId) {
        conversationService.endConversation(getCurrentUserId(), conversationId);
        return ResponseEntity.noContent().build();
    }
}