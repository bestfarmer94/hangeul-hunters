package com.example.hangeulhunters.domain.user.repository;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserStatRow {

    private String nickname;
    private String email;
    private long rolePlayCount;
    private long reportCount;
    private long askCount;
}
