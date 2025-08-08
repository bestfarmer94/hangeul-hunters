package com.example.hangeulhunters.presentation.conversation;

import com.example.hangeulhunters.application.common.dto.PageResponse;
import com.example.hangeulhunters.application.conversation.dto.MessageDto;
import com.example.hangeulhunters.application.conversation.dto.MessageRequest;
import com.example.hangeulhunters.application.conversation.service.MessageService;
import com.example.hangeulhunters.presentation.common.ControllerSupport;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController extends ControllerSupport {

    private final MessageService messageService;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PageResponse<MessageDto>> getMessagesByConversation(
            @Parameter(description = "대화 id") @RequestParam Long conversationId,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "페이지당 수량") @RequestParam(defaultValue = "10") int size) {
        PageResponse<MessageDto> messages = messageService.getMessagesByConversationId(getCurrentUserId(), conversationId, page, size);
        return ResponseEntity.ok(messages);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageDto> sendMessage(@Valid @RequestBody MessageRequest request) {
        MessageDto message = messageService.sendMessage(getCurrentUserId(), request);
        return ResponseEntity.ok(message);
    }
}