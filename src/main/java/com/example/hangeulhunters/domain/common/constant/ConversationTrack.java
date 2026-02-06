package com.example.hangeulhunters.domain.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ConversationTrack {
    CAREER("Career"),
    FAMILY("Family"),
    BELONGING("Belonging"),
    KPOP("K-POP");

    private final String displayName;
}
