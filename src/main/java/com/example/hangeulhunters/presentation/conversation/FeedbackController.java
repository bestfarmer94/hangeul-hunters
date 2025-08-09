package com.example.hangeulhunters.presentation.conversation;

import com.example.hangeulhunters.application.conversation.dto.FeedbackDto;
import com.example.hangeulhunters.application.conversation.service.FeedbackService;
import com.example.hangeulhunters.presentation.common.ControllerSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feedbacks")
@RequiredArgsConstructor
public class FeedbackController extends ControllerSupport {

    private final FeedbackService feedbackService;

    @PostMapping("/message/{messageId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(
            summary = "메시지 단위 피드백 생성/조회",
            description = "특정 메시지를 피드백을 받거나 조회합니다.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<FeedbackDto> feedbackMessage(
            @PathVariable Long messageId
    ) {
        FeedbackDto feedback = feedbackService.feedbackMessage(getCurrentUserId(), messageId);
        return ResponseEntity.ok(feedback);
    }
}