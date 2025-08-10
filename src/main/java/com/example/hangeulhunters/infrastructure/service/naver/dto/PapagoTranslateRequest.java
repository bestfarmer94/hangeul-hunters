package com.example.hangeulhunters.infrastructure.service.naver.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PapagoTranslateRequest {
    private String source;
    private String target;
    private String text;

    public static PapagoTranslateRequest of(String source, String target, String content) {
        return PapagoTranslateRequest.builder()
                .source(source)
                .target(target)
                .text(content)
                .build();
    }
}
