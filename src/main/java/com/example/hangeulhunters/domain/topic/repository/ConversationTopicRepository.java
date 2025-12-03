package com.example.hangeulhunters.domain.topic.repository;

import com.example.hangeulhunters.domain.conversation.constant.ConversationType;
import com.example.hangeulhunters.domain.topic.entity.ConversationTopic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConversationTopicRepository extends JpaRepository<ConversationTopic, Long> {

    /**
     * 대화 타입과 트랙으로 주제 조회
     */
    Optional<ConversationTopic> findByConversationTypeAndTrack(ConversationType conversationType, String track);

    /**
     * 대화 타입으로 주제 조회 (면접용)
     */
    Optional<ConversationTopic> findByConversationTypeAndTrackIsNull(ConversationType conversationType);
}
