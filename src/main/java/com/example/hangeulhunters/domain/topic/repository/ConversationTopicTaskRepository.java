package com.example.hangeulhunters.domain.topic.repository;

import com.example.hangeulhunters.domain.topic.entity.ConversationTopicTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationTopicTaskRepository extends JpaRepository<ConversationTopicTask, Long> {

    /**
     * 주제 ID로 모든 task 조회 (레벨 순으로 정렬)
     */
    List<ConversationTopicTask> findByTopicIdOrderByLevelAsc(Long topicId);

    Optional<ConversationTopicTask> findByTopicIdAndLevelAndDeletedAtNull(Long topicId, Integer taskLevel);
}
