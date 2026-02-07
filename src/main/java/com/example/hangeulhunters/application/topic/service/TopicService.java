package com.example.hangeulhunters.application.topic.service;

import com.example.hangeulhunters.application.common.dto.PageResponse;
import com.example.hangeulhunters.application.topic.dto.ConversationTopicDto;
import com.example.hangeulhunters.domain.topic.entity.UserFavoriteTopic;
import com.example.hangeulhunters.domain.topic.repository.ConversationTopicRepository;
import com.example.hangeulhunters.domain.topic.repository.UserFavoriteTopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Topic 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TopicService {

    private final ConversationTopicRepository conversationTopicRepository;
    private final UserFavoriteTopicRepository userFavoriteTopicRepository;

    /**
     * 주제 목록 조회
     * 
     * @param userId        사용자 ID
     * @param track         트랙 (null이면 전체 조회)
     * @param favoritesOnly 즐겨찾기만 조회 여부
     * @return 주제 목록
     */
    @Transactional(readOnly = true)
    public List<ConversationTopicDto> getTopics(Long userId, String track, Boolean favoritesOnly) {
        return conversationTopicRepository.getTopics(userId, track, favoritesOnly);
    }

    /**
     * 주제 이름을 통한 정보 조회
     *
     * @param topicName 주제 이름
     * @return 주제 정보
     */
    @Transactional(readOnly = true)
    public ConversationTopicDto getTopicByName(String topicName) {
        return conversationTopicRepository.findByNameAndDeletedAtNull(topicName)
                .map(ConversationTopicDto::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("Topic not found"));
    }

    /**
     * 즐겨찾기 추가
     * 
     * @param userId  사용자 ID
     * @param topicId 주제 ID
     */
    @Transactional
    public void addFavoriteTopic(Long userId, Long topicId) {
        // 즐겨찾기 엔티티 조회 또는 생성
        UserFavoriteTopic favoriteTopic = userFavoriteTopicRepository
                // 삭제된 즐겨찾기 data 존재 여부 확인
                .findOneByUserIdAndTopicId(userId, topicId)
                // 복구
                .map(deletedFavoriteTopic -> {
                    deletedFavoriteTopic.restore();
                    return deletedFavoriteTopic;
                })
                // 없으면 신규 생성
                .orElseGet(() -> UserFavoriteTopic.builder()
                        .userId(userId)
                        .topicId(topicId)
                        .build());

        userFavoriteTopicRepository.save(favoriteTopic);
    }

    /**
     * 즐겨찾기 제거
     * 
     * @param userId  사용자 ID
     * @param topicId 주제 ID
     */
    @Transactional
    public void removeFavoriteTopic(Long userId, Long topicId) {
        UserFavoriteTopic favoriteTopic = userFavoriteTopicRepository
                .findOneByUserIdAndTopicId(userId, topicId)
                .orElseThrow(() -> new IllegalArgumentException("Favorite topic not found"));

        // Soft delete
        favoriteTopic.delete(userId);
        userFavoriteTopicRepository.save(favoriteTopic);
    }

    /**
     * 최근 사용한 주제 목록 조회
     *
     * @param userId 사용자 ID
     * @param page   페이지 번호 (1부터 시작)
     * @param size   페이지 크기
     * @return 최근 사용한 주제 목록
     */
    @Transactional(readOnly = true)
    public PageResponse<ConversationTopicDto> getRecentlyUsedTopics(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<ConversationTopicDto> topicsPage = conversationTopicRepository.getRecentlyUsedTopics(userId, pageable);
        return com.example.hangeulhunters.application.common.dto.PageResponse.of(topicsPage, topicsPage.getContent());
    }
}
