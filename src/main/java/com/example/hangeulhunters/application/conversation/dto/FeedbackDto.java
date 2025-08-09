package com.example.hangeulhunters.application.conversation.dto;

import com.example.hangeulhunters.domain.conversation.constant.FeedbackTarget;
import com.example.hangeulhunters.domain.conversation.entity.Feedback;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackDto {
    private Long feedbackId;
    private FeedbackTarget target;
    private Long targetId;
    private Integer honorificScore;
    private Integer naturalnessScore;
    private Integer pronunciationScore;
    private String content;

    public static FeedbackDto fromEntity(Feedback feedback) {
        return FeedbackDto.builder()
                .feedbackId(feedback.getId())
                .target(feedback.getTarget())
                .targetId(feedback.getTargetId())
                .honorificScore(feedback.getHonorificScore())
                .naturalnessScore(feedback.getNaturalnessScore())
                .pronunciationScore(feedback.getPronunciationScore())
                .content(feedback.getContent())
                .build();
    }
}