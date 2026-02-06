package com.example.hangeulhunters.application.topic.dto;

import com.example.hangeulhunters.domain.topic.entity.ConversationTopic;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Topic DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "주제 정보 DTO")
public class ConversationTopicDto {

    @Schema(description = "주제 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long topicId;

    @Schema(description = "주제 이름", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "카테고리", requiredMode = Schema.RequiredMode.REQUIRED)
    private String category;

    @Schema(description = "주제 설명", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String description;

    @Schema(description = "주제 이미지 URL", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String imageUrl;

    @Schema(description = "즐겨찾기 여부", requiredMode = Schema.RequiredMode.REQUIRED)
    private Boolean isFavorite;

    public static ConversationTopicDto fromEntity(ConversationTopic topic) {
        return ConversationTopicDto.builder()
                .topicId(topic.getId())
                .name(topic.getName())
                .category(topic.getTrack())
                .description(topic.getDescription())
                .imageUrl(topic.getImageUrl())
                .isFavorite(false) // 기본값, 실제 값은 서비스 레이어에서 설정
                .build();
    }
}
