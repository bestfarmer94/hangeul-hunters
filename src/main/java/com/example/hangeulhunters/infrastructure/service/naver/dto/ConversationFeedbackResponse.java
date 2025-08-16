package com.example.hangeulhunters.infrastructure.service.naver.dto;

import lombok.Data;

@Data
public class ConversationFeedbackResponse {
    private String summary;
    private String goodPoints;
    private String improvementPoints;
    private String improvementExamples;
    private String overallEvaluation;
}
