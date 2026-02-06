package com.example.hangeulhunters.domain.topic.repository;

import com.example.hangeulhunters.application.topic.dto.TopicDto;
import com.example.hangeulhunters.domain.topic.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Topic 레포지토리
 */
@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {

    /**
     * 활성화된 주제 목록을 즐겨찾기 우선으로 정렬하여 조회
     * 즐겨찾기한 주제가 먼저 나오고, 그 다음 display_order 순으로 정렬
     */
    @Query("""
            SELECT
                NEW com.example.hangeulhunters.application.topic.dto.TopicDto(
                    t.id,
                    t.name,
                    t.category,
                    t.description,
                    t.imageUrl,
                    CASE WHEN f.id IS NOT NULL THEN TRUE ELSE FALSE END
                )
            FROM
                Topic t
                LEFT JOIN UserFavoriteTopic f
                    ON t.id = f.topicId
                        AND f.userId = :userId
                        AND f.deletedAt IS NULL
            WHERE
                t.deletedAt IS NULL
                AND (:category IS Null OR t.category = :category)
                AND (:favoritesOnly = FALSE OR f.id IS NOT NULL)
            ORDER BY
                CASE WHEN f.id IS NOT NULL THEN 0 ELSE 1 END, t.displayOrder ASC
            """)
    List<TopicDto> getTopics(
            @Param("userId") Long userId,
            @Param("category") String category,
            @Param("favoritesOnly") Boolean favoritesOnly
    );
}
