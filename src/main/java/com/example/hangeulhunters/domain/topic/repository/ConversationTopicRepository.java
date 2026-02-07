package com.example.hangeulhunters.domain.topic.repository;

import com.example.hangeulhunters.application.topic.dto.ConversationTopicDto;
import com.example.hangeulhunters.domain.topic.entity.ConversationTopic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationTopicRepository extends JpaRepository<ConversationTopic, Long> {

    Optional<ConversationTopic> findByNameAndDeletedAtNull(String topicName);

    /**
     * 활성화된 주제 목록을 즐겨찾기 우선으로 정렬하여 조회
     * 즐겨찾기한 주제가 먼저 나오고, 그 다음 display_order 순으로 정렬
     */
    @Query("""
            SELECT
                NEW com.example.hangeulhunters.application.topic.dto.ConversationTopicDto(
                    t.id,
                    t.name,
                    t.track,
                    t.description,
                    t.imageUrl,
                    CASE WHEN f.id IS NOT NULL THEN TRUE ELSE FALSE END
                )
            FROM
                ConversationTopic t
                LEFT JOIN UserFavoriteTopic f
                    ON t.id = f.topicId
                        AND f.userId = :userId
                        AND f.deletedAt IS NULL
            WHERE
                t.deletedAt IS NULL
                AND (:track IS Null OR t.track = :track)
                AND (:favoritesOnly = FALSE OR f.id IS NOT NULL)
            ORDER BY
                CASE WHEN f.id IS NOT NULL THEN 0 ELSE 1 END, t.displayOrder ASC
            """)
    List<ConversationTopicDto> getTopics(
            @Param("userId") Long userId,
            @Param("track") String track,
            @Param("favoritesOnly") Boolean favoritesOnly);

    /**
     * 최근 사용한 주제 목록을 페이징 조회
     * 사용자가 대화를 진행한 주제를 최근 활동 시간 기준으로 정렬하여 반환
     */
    @Query("""
            SELECT
                NEW com.example.hangeulhunters.application.topic.dto.ConversationTopicDto(
                    t.id,
                    t.name,
                    t.track,
                    t.description,
                    t.imageUrl,
                    CASE WHEN f.id IS NOT NULL THEN TRUE ELSE FALSE END
                )
            FROM
                ConversationTopic t
                INNER JOIN Conversation c
                    ON t.name = c.conversationTopic
                        AND c.userId = :userId
                        AND c.deletedAt IS NULL
                LEFT JOIN UserFavoriteTopic f
                    ON t.id = f.topicId
                        AND f.userId = :userId
                        AND f.deletedAt IS NULL
            WHERE
                t.deletedAt IS NULL
            GROUP BY
                t.id, t.name, t.track, t.description, t.imageUrl, f.id
            ORDER BY
                MAX(c.lastActivityAt) DESC
            """)
    Page<ConversationTopicDto> getRecentlyUsedTopics(@Param("userId") Long userId, Pageable pageable);
}
