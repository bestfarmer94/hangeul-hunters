package com.example.hangeulhunters.presentation.conversation;

import com.example.hangeulhunters.application.common.dto.PageResponse;
import com.example.hangeulhunters.application.conversation.dto.MessageDto;
import com.example.hangeulhunters.application.conversation.dto.MessageFeedbackDto;
import com.example.hangeulhunters.application.conversation.dto.MessageRequest;
import com.example.hangeulhunters.application.conversation.service.FeedbackService;
import com.example.hangeulhunters.application.conversation.service.MessageService;
import com.example.hangeulhunters.infrastructure.service.naver.dto.HonorificVariationsResponse;
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

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Tag(name = "Message", description = "Message API")
public class MessageController extends ControllerSupport {

    private final MessageService messageService;
    private final FeedbackService feedbackService;

    @GetMapping
    @Operation(
            summary = "대화방 메시지 조회",
            description = "특정 대화방의 메시지를 페이지 단위로 조회합니다",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<PageResponse<MessageDto>> getMessagesByConversation(
            @Parameter(description = "대화 id") @RequestParam Long conversationId,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "페이지당 수량") @RequestParam(defaultValue = "10") int size) {
        PageResponse<MessageDto> messages = messageService.getMessagesByConversationId(getCurrentUserId(), conversationId, page, size);
        return ResponseEntity.ok(messages);
    }

    @PostMapping
    @Operation(
            summary = "메시지 전송",
            description = "대화에 메시지를 전송합니다. 사용자가 보낸 메시지와 시스템이 생성한 응답 메시지를 모두 반환합니다",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<List<MessageDto>> sendMessage(@Valid @RequestBody MessageRequest request) {
        List<MessageDto> messages = messageService.sendMessage(getCurrentUserId(), request);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/ai-reply")
    @Operation(
            summary = "메시지 생성 (AI 응답)",
            description = "AI를 사용하여 대화에 자동 응답 메시지를 생성합니다",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<MessageDto> createAiReply(@Parameter(description = "대화 id") @RequestParam Long conversationId) {
        MessageDto message = messageService.createAiReply(getCurrentUserId(), conversationId);
        return ResponseEntity.ok(message);
    }

    @PutMapping("{messageId}/translate")
    @Operation(
            summary = "메시지 번역",
            description = "특정 메시지를 번역합니다",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<String> translateMessage(
            @Parameter(description = "메시지 id") @PathVariable Long messageId) {
        String translatedMessage = messageService.translateMessage(messageId);
        return ResponseEntity.ok(translatedMessage);
    }

    @GetMapping("/{messageId}/honorific-variations")
    @Operation(
            summary = "여러가지 존댓말 표현 생성",
            description = "주어진 원문에 대해 다양한 존댓말 표현을 생성합니다",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<HonorificVariationsResponse.ExpressionsByFormality> honorificVariations(
            @Parameter(description = "메시지 id") @PathVariable Long messageId) {
        return ResponseEntity.ok(messageService.generateHonorificVariations(getCurrentUserId(), messageId));
    }

    @PutMapping("{messageId}/tts")
    @Operation(
            summary = "특정 메시지를 음성으로 변환",
            description = "특정 메세지를 음성 데이터로 변환합니다",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<String> convertMessageToSpeech(
            @Parameter(description = "메시지 id") @PathVariable Long messageId) {
        String ttsAudioUrl = messageService.convertMessageToSpeech(getCurrentUserId(), messageId);
        return ResponseEntity.ok(ttsAudioUrl);
    }
}