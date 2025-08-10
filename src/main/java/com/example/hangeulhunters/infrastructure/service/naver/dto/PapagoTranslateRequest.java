package com.example.hangeulhunters.infrastructure.service.naver.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PapagoTranslateRequest {
    private String source;
    private String target;
    private String text;

    public static PapagoTranslateRequest translateKoToEn(String content) {
        return PapagoTranslateRequest.builder()
                .source("ko")
                .target("en")
                .text(content)
                .build();
    }
}
