package com.example.hangeulhunters.infrastructure.service.naver.dto;

import lombok.Getter;

@Getter
public class ClovaStudioCommonResponse {
    private Status status;
    private Result result;

    @Getter
    public static class Status {
        private String code;
        private String message;
    }

    @Getter
    public static class Result {
        private Message message;

        @Getter
        public static class Message {
            private String role;
            private String content;
        }
    }
}
