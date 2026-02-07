package com.example.hangeulhunters.presentation.topic;

import com.example.hangeulhunters.application.common.dto.PageResponse;
import com.example.hangeulhunters.application.topic.dto.ConversationTopicDto;
import com.example.hangeulhunters.application.topic.service.TopicService;
import com.example.hangeulhunters.presentation.common.ControllerSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Topic 컨트롤러
 */
@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
@Tag(name = "Topic", description = "주제 API")
public class TopicController extends ControllerSupport {

    private final TopicService topicService;

    /**
     * 주제 목록 조회
     *
     * @param category      카테고리 (null이면 전체 조회)
     * @param favoritesOnly 즐겨찾기만 조회 여부 (기본값: false)
     * @return 주제 목록
     */
    @GetMapping
    @Operation(summary = "주제 목록 조회", description = "주제 목록을 조회합니다. 카테고리 필터링 및 즐겨찾기 필터링이 가능하며, 즐겨찾기한 주제가 상단에 표시됩니다.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<ConversationTopicDto>> getTopics(
            @Parameter(description = "카테고리 (null이면 전체 조회)") @RequestParam(required = false) String category,
            @Parameter(description = "즐겨찾기만 조회 여부") @RequestParam(required = false, defaultValue = "false") Boolean favoritesOnly) {
        List<ConversationTopicDto> topics = topicService.getTopics(getCurrentUserId(), category, favoritesOnly);
        return ResponseEntity.ok(topics);
    }

    /**
     * 즐겨찾기 추가
     *
     * @param topicId 주제 ID
     * @return 응답 엔티티
     */
    @PostMapping("/{topicId}/favorite")
    @Operation(summary = "즐겨찾기 추가", description = "주제를 즐겨찾기에 추가합니다", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> addFavorite(@PathVariable Long topicId) {
        topicService.addFavoriteTopic(getCurrentUserId(), topicId);
        return ResponseEntity.ok().build();
    }

    /**
     * 즐겨찾기 제거
     *
     * @param topicId 주제 ID
     * @return 응답 엔티티
     */
    @DeleteMapping("/{topicId}/favorite")
    @Operation(summary = "즐겨찾기 제거", description = "주제를 즐겨찾기에서 제거합니다", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> removeFavorite(@PathVariable Long topicId) {
        topicService.removeFavoriteTopic(getCurrentUserId(), topicId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 최근 사용한 주제 목록 조회
     *
     * @param page 페이지 번호 (기본값: 1)
     * @param size 페이지 크기 (기본값: 10)
     * @return 최근 사용한 주제 목록
     */
    @GetMapping("/recent")
    @Operation(summary = "최근 사용한 주제 목록 조회", description = "사용자가 최근에 대화를 진행한 주제 목록을 조회합니다. 즐겨찾기 여부가 포함됩니다.", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<PageResponse<ConversationTopicDto>> getRecentlyUsedTopics(
            @Parameter(description = "페이지 번호") @RequestParam(required = false, defaultValue = "1") Integer page,
            @Parameter(description = "페이지 크기") @RequestParam(required = false, defaultValue = "10") Integer size) {
        PageResponse<ConversationTopicDto> topics = topicService.getRecentlyUsedTopics(getCurrentUserId(), page, size);
        return ResponseEntity.ok(topics);
    }
}
