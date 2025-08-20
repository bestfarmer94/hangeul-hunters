package com.example.hangeulhunters.domain.persona.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Relationship {
    BOSS("boss", "employee", "Distant"),
    GF_PARENTS("girlfriend's parents","daughter's boyfriend", "Distant"),
    CLERK("cafe clerk","customer", "medium");

    private final String aiRole;
    private final String userRole;
    private final String intimacyLevel;

    public static Relationship ofAiRole(String aiRole) {
        for (Relationship relationship : values()) {
            if (relationship.getAiRole().equalsIgnoreCase(aiRole)) {
                return relationship;
            }
        }
        throw new IllegalArgumentException("Unknown aiRole: " + aiRole);
    }
}
