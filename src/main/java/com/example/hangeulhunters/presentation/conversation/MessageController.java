package com.example.hangeulhunters.presentation.conversation;

import com.example.hangeulhunters.application.common.dto.PageResponse;
import com.example.hangeulhunters.application.conversation.dto.MessageDto;
import com.example.hangeulhunters.application.conversation.dto.MessageFeedbackDto;
import com.example.hangeulhunters.application.conversation.dto.MessageRequest;
import com.example.hangeulhunters.application.conversation.dto.MessageSendResponse;
import com.example.hangeulhunters.application.conversation.service.FeedbackService;
import com.example.hangeulhunters.application.conversation.service.MessageService;
import com.example.hangeulhunters.presentation.common.ControllerSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Tag(name = "Message", description = "Message API")
public class MessageController extends ControllerSupport {

        private final MessageService messageService;
        private final FeedbackService feedbackService;

        @GetMapping
        @Operation(summary = "대화방 메시지 조회", description = "특정 대화방의 메시지를 페이지 단위로 조회합니다", security = @SecurityRequirement(name = "bearerAuth"))
        public ResponseEntity<PageResponse<MessageDto>> getMessagesByConversation(
                        @Parameter(description = "대화 id") @RequestParam Long conversationId,
                        @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "1") int page,
                        @Parameter(description = "페이지당 수량") @RequestParam(defaultValue = "10") int size) {
                PageResponse<MessageDto> messages = messageService.getMessagesByConversationId(getCurrentUserId(),
                                conversationId, page, size);
                return ResponseEntity.ok(messages);
        }

        @PutMapping("{messageId}/translate")
        @Operation(summary = "메시지 번역", description = "특정 메시지를 번역합니다", security = @SecurityRequirement(name = "bearerAuth"))
        public ResponseEntity<String> translateMessage(
                        @Parameter(description = "메시지 id") @PathVariable Long messageId) {
                String translatedMessage = messageService.translateMessage(messageId);
                return ResponseEntity.ok(translatedMessage);
        }

        @GetMapping("/{messageId}/feedback")
        @Operation(summary = "메시지 피드백 조회", description = "특정 메시지에 대한 피드백을 조회합니다", security = @SecurityRequirement(name = "bearerAuth"))
        public ResponseEntity<MessageFeedbackDto> getMessageFeedback(
                        @Parameter(description = "메시지 id") @PathVariable Long messageId) {
                return ResponseEntity.ok(feedbackService.getMessageFeedback(messageId));
        }

        @PutMapping("{messageId}/tts")
        @Operation(summary = "특정 메시지를 음성으로 변환", description = "특정 메세지를 음성 데이터로 변환합니다", security = @SecurityRequirement(name = "bearerAuth"))
        public ResponseEntity<String> convertMessageToSpeech(
                        @Parameter(description = "메시지 id") @PathVariable Long messageId) {
                String ttsAudioUrl = messageService.convertMessageToSpeech(getCurrentUserId(), messageId);
                return ResponseEntity.ok(ttsAudioUrl);
        }

        @PostMapping("/roleplay")
        @Operation(summary = "롤플레이 메시지 전송", description = "롤플레이 대화에 메시지를 전송합니다. 사용자가 보낸 메시지와 AI 응답 메시지를 모두 반환합니다.", security = @SecurityRequirement(name = "bearerAuth"))
        public ResponseEntity<List<MessageDto>> sendRolePlayMessage(@Valid @RequestBody MessageRequest request) {
                // 응답 메시지 리스트
                List<MessageDto> responseMessageList = new ArrayList<>();

                // 1. 사용자 메시지 저장
                MessageDto userMessage = messageService.sendMessage(getCurrentUserId(), request);
                responseMessageList.add(userMessage);

                // 2. AI 처리 및 응답
                List<MessageDto> aiResponse = messageService.processRolePlayWithAi(
                                getCurrentUserId(),
                                request.getConversationId(),
                                userMessage.getMessageId(),
                                request.getContent());
                responseMessageList.addAll(aiResponse);

                return ResponseEntity.ok(responseMessageList);
        }

        @PostMapping("/ask")
        @Operation(summary = "ASK 후속 메시지 전송", description = "ASK 대화에 후속 메시지를 전송합니다. 사용자가 보낸 메시지와 AI 응답 메시지를 모두 반환합니다. (임시로 JSON 응답)", security = @SecurityRequirement(name = "bearerAuth"))
        public ResponseEntity<List<MessageDto>> sendAskMessage(@Valid @RequestBody MessageRequest request) {
                // 응답 메시지 리스트
                List<MessageDto> responseMessageList = new ArrayList<>();

                // 1. 사용자 메시지 저장
                MessageDto userMessage = messageService.sendMessage(getCurrentUserId(), request);
                responseMessageList.add(userMessage);

                // 2. AI 처리 및 응답
                MessageDto aiResponse = messageService.processAskChatWithAi(
                                getCurrentUserId(),
                                request.getConversationId(),
                                request.getContent());
                responseMessageList.add(aiResponse);

                return ResponseEntity.ok(responseMessageList);
        }
}