package com.example.hangeulhunters.domain.topic.repository;

import com.example.hangeulhunters.domain.topic.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Topic 레포지토리
 */
@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
}
