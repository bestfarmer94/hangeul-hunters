package com.example.hangeulhunters.presentation.conversation;

import com.example.hangeulhunters.application.common.dto.PageResponse;
import com.example.hangeulhunters.application.conversation.dto.*;
import com.example.hangeulhunters.application.conversation.dto.AskRequest;
import com.example.hangeulhunters.application.conversation.service.ConversationService;
import com.example.hangeulhunters.application.conversation.service.FeedbackService;
import com.example.hangeulhunters.application.conversation.service.MessageService;
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
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
@Tag(name = "Conversation", description = "Conversation API")
public class ConversationController extends ControllerSupport {

    private final ConversationService conversationService;
    private final MessageService messageService;
    private final FeedbackService feedbackService;

    @GetMapping("/{conversationId}")
    @Operation(summary = "대화방 상세 조회", description = "특정 대화방의 정보를 조회합니다", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ConversationDto> getConversationById(
            @Parameter(description = "대화방 ID") @PathVariable Long conversationId) {

        ConversationDto conversation = conversationService.getConversationById(getCurrentUserId(), conversationId);
        return ResponseEntity.ok(conversation);
    }

    @GetMapping
    @Operation(summary = "현재 사용자의 대화 목록 조회", description = "로그인한 사용자의 대화 목록을 필터링하여 페이징 조회합니다", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<PageResponse<ConversationDto>> getUserConversations(
            @Parameter(description = "대화 상태") @RequestParam(required = false) ConversationStatus status,
            @Parameter(description = "AI 페르소나 ID") @RequestParam(required = false) Long personaId,
            @Parameter(description = "정렬 기준") @RequestParam(defaultValue = "LAST_ACTIVITY_DESC") ConversationSortBy sortBy,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") Integer size) {

        PageResponse<ConversationDto> conversations = conversationService.getUserConversations(
                getCurrentUserId(),
                ConversationFilterRequest.builder()
                        .status(status)
                        .personaId(personaId)
                        .sortBy(sortBy)
                        .page(page)
                        .size(size)
                        .build());
        return ResponseEntity.ok(conversations);
    }

    @PostMapping("/role-playing")
    @Operation(summary = "새 롤플레잉 대화 생성", description = "AI 페르소나와의 새로운 롤플레잉 대화를 시작합니다", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ConversationDto> createConversation(@Valid @RequestBody ConversationRequest request) {
        ConversationDto conversation = conversationService.createRolePlaying(getCurrentUserId(), request);
        messageService.createRolePlayingFirstMessage(getCurrentUserId(), conversation);
        return ResponseEntity.ok(conversation);
    }

    @PostMapping("/ask")
    @Operation(summary = "ASK 타입 대화 생성 (SSE 스트림)", description = "페르소나 없이 질문을 위한 대화를 생성하고 AI 응답을 스트림으로 반환합니다", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<MessageDto> createAsk(@Valid @RequestBody AskRequest request) {
        // 1. 대화 생성
        ConversationDto conversation = conversationService.createAsk(getCurrentUserId(), request);

        // 2. 초기 메시지 생성
        messageService.createAskInitialMessages(
                getCurrentUserId(),
                conversation.getConversationId(),
                request);

        // 3. AI 메시지를 SSE 스트림으로 실행
        Flux<ServerSentEvent<String>> stream = messageService.createAskFirstMessageStream(
                getCurrentUserId(),
                conversation,
                request.getAskTarget(),
                request.getCloseness(),
                request.getSituation());

        // 3. 스트림 완료까지 대기 (임시 코드)
        stream.blockLast();

        // 4. 저장된 메시지 조회 및 반환 (임시 코드)
        MessageDto message = messageService.getLastMessageInConversation(conversation.getConversationId());
        return ResponseEntity.ok(message);
    }

    @PutMapping("/{conversationId}/end")
    @Operation(summary = "대화 종료", description = "진행 중인 대화를 종료합니다", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> endConversation(@PathVariable Long conversationId) {
        conversationService.endConversation(getCurrentUserId(), conversationId);
        feedbackService.saveConversationFeedback(getCurrentUserId(),
                conversationService.getConversationById(getCurrentUserId(), conversationId),
                messageService.getAllMessagesByConversationId(conversationId));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{conversationId}")
    @Operation(summary = "대화 삭제", description = "대화를 삭제합니다. 실제로 데이터베이스에서 삭제되지 않고 삭제 상태로 변경됩니다.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> deleteConversation(@PathVariable Long conversationId) {
        conversationService.deleteConversation(getCurrentUserId(), conversationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{conversationId}/feedback")
    @Operation(summary = "대화 전체 피드백 조회", description = "대화 전체에 대한 피드백을 조회합니다.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ConversationFeedbackDto> getConversationFeedback(
            @PathVariable Long conversationId) {
        ConversationFeedbackDto feedback = feedbackService.getConversationFeedback(conversationId);
        return ResponseEntity.ok(feedback);
    }

    @PostMapping("/{conversationId}/feedback")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "대화 전체 피드백 생성", description = "대화 전체에 대한 피드백을 생성합니다. (종료된 대화에 피드백이 없을 때 사용)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ConversationFeedbackDto> createConversationFeedback(
            @PathVariable Long conversationId) {
        ConversationFeedbackDto feedback = feedbackService.saveConversationFeedback(getCurrentUserId(),
                conversationService.getConversationById(getCurrentUserId(), conversationId),
                messageService.getAllMessagesByConversationId(conversationId));
        return ResponseEntity.ok(feedback);
    }

    @PostMapping("/interview")
    @Operation(summary = "면접 대화 생성", description = "면접 대화를 생성합니다. 면접관 페르소나는 자동으로 생성됩니다.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ConversationDto> createInterview(@Valid @RequestBody InterviewRequest request) {
        ConversationDto conversation = conversationService.createInterview(getCurrentUserId(), request);
        messageService.createInterviewFirstMessage(getCurrentUserId(), conversation);
        return ResponseEntity.ok(conversation);
    }
}