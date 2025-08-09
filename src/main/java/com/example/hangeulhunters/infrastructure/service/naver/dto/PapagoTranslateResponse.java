package com.example.hangeulhunters.infrastructure.service.naver.dto;

import lombok.Getter;

@Getter
public class PapagoTranslateResponse {
    private Message message;

    @Getter
    public static class Message {
        private Result result;

        @Getter
        public static class Result {
            private String srcLangType;
            private String tarLangType;
            private String translatedText;
        }
    }
}
