package com.example.hangeulhunters.infrastructure.service.naver.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClovaSpeechSTTRequest {
    private String lang;
    private String script;
    private String assessment;
}
