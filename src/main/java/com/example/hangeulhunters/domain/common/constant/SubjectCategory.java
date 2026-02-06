package com.example.hangeulhunters.domain.common.constant;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SubjectCategory {
    DAILY_CONVERSATION("Daily Conversation"),
    BUSINESS("Business"),
    TRAVEL("Travel"),
    K_DRAMA("K-Drama"),
    K_POP("K-Pop"),
    ETIQUETTE("Etiquette"),
    INTERNET_SLANG("Internet Slang"),
    FOOD("Food"),
    ORDERING("Ordering"),
    BEAUTY("Beauty"),
    GATHERING("Gathering");

    private final String name;
}
