package com.example.hangeulhunters.domain.common.constant;

/**
 * 이미지 타입
 * 이미지 저장 경로를 결정하는 타입 상수
 */
public enum ImageType {
    USER_PROFILE("profiles/users/"),
    PERSONA_PROFILE("profiles/personas/");
    
    private final String path;
    
    ImageType(String path) {
        this.path = path;
    }
    
    public String getPath() {
        return path;
    }
}