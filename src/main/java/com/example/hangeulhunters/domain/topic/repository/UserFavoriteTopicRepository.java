package com.example.hangeulhunters.domain.topic.repository;

import com.example.hangeulhunters.domain.topic.entity.UserFavoriteTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserFavoriteTopic 레포지토리
 */
@Repository
public interface UserFavoriteTopicRepository extends JpaRepository<UserFavoriteTopic, Long> {
    Optional<UserFavoriteTopic> findOneByUserIdAndTopicId(Long userId, Long topicId);
}
