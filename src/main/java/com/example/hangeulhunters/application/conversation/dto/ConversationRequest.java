package com.example.hangeulhunters.application.conversation.dto;

import com.example.hangeulhunters.domain.conversation.constant.ConversationTopicExample;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationRequest {

    @NotNull(message = "대화 주제는 필수입니다.")
    @Schema(description = "대화 주제")
    private ConversationTopicExample conversationTopic;

    @Schema(description = "부연 상황 설명")
    private String details;
}