package com.example.hangeulhunters.domain.common.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Closeness {
    CASUAL("Casual"),
    FRIENDLY("Friendly"),
    PROFESSIONAL("Professional"),
    FORMAL("Formal");

    private final String displayName;

    public Boolean isValid(String closeness) {
        for (Closeness c : Closeness.values()) {
            if (c.getDisplayName().equals(closeness)) {
                return true;
            }
        }
        return false;
    }
}
