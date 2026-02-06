package com.example.hangeulhunters.application.topic.service;

import com.example.hangeulhunters.application.topic.dto.TopicDto;
import com.example.hangeulhunters.domain.topic.entity.UserFavoriteTopic;
import com.example.hangeulhunters.domain.topic.repository.TopicRepository;
import com.example.hangeulhunters.domain.topic.repository.UserFavoriteTopicRepository;
import lombok.RequiredArgsConstructor;
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

    private final TopicRepository topicRepository;
    private final UserFavoriteTopicRepository userFavoriteTopicRepository;

    /**
     * 주제 목록 조회
     * 
     * @param userId        사용자 ID
     * @param category      카테고리 (null이면 전체 조회)
     * @param favoritesOnly 즐겨찾기만 조회 여부
     * @return 주제 목록
     */
    public List<TopicDto> getTopics(Long userId, String category, Boolean favoritesOnly) {
        return topicRepository.getTopics(userId, category, favoritesOnly);
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
                        .build()
                );

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
}
