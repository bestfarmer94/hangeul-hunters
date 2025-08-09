package com.example.hangeulhunters.domain.persona.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Relationship {
    BOSS("boss", "employee"),
    GF_PARENTS("girlfriend's parents","daughter's boyfriend"),
    CLERK("cafe clerk","customer");

    private final String aiRole;
    private final String userRole;
}
