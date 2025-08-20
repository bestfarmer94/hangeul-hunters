package com.example.hangeulhunters.infrastructure.service.naver.dto;

import lombok.Data;

@Data
public class ClovaSpeechSTTResponse {
    private String text;
    private Integer assessment_score;
}
