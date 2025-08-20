package com.example.hangeulhunters.domain.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 오디오 타입
 * 오디오 저장 경로를 결정하는 타입 상수
 */
@Getter
@RequiredArgsConstructor
public enum AudioType {
    MESSAGE_AUDIO("audio/messages/"),;

    private final String path;
}
