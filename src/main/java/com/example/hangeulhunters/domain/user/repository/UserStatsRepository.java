package com.example.hangeulhunters.domain.user.repository;

import com.example.hangeulhunters.domain.user.entity.UserStatRow;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserStatsRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<UserStatRow> findUserStats() {
        String sql = """
                SELECT
                    u.nickname,
                    u.email,
                    (SELECT COUNT(*)
                     FROM conversation c
                     WHERE c.user_id = u.id
                       AND c.conversation_type = 'ROLE_PLAYING'
                       AND c.deleted_at IS NULL
                    ) AS role_play_count,
                    (SELECT COUNT(*)
                     FROM conversation c
                     WHERE c.user_id = u.id
                       AND c.conversation_type = 'ROLE_PLAYING'
                       AND c.status = 'ENDED'
                       AND c.deleted_at IS NULL
                    ) AS report_count,
                    (SELECT COUNT(*)
                     FROM conversation c
                     WHERE c.user_id = u.id
                       AND c.conversation_type = 'ASK'
                       AND c.deleted_at IS NULL
                    ) AS ask_count
                FROM users u
                ORDER BY role_play_count DESC
                """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> new UserStatRow(
                rs.getString("nickname"),
                rs.getString("email"),
                rs.getLong("role_play_count"),
                rs.getLong("report_count"),
                rs.getLong("ask_count")));
    }
}
