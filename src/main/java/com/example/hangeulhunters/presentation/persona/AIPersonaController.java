package com.example.hangeulhunters.presentation.persona;

import com.example.hangeulhunters.application.common.dto.PageResponse;
import com.example.hangeulhunters.application.persona.dto.AIPersonaDto;
import com.example.hangeulhunters.application.persona.dto.AIPersonaRequest;
import com.example.hangeulhunters.application.persona.service.AIPersonaService;
import com.example.hangeulhunters.presentation.common.ControllerSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * AI 페르소나 컨트롤러
 */
@RestController
@RequestMapping("/api/personas")
@RequiredArgsConstructor
@Tag(name = "AI Persona", description = "AI 페르소나 API")
public class AIPersonaController extends ControllerSupport {

    private final AIPersonaService aiPersonaService;

    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "내 AI 페르소나 목록 조회 (페이징)",
        description = "현재 로그인한 사용자의 AI 페르소나 목록을 페이징하여 조회합니다",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<PageResponse<AIPersonaDto>> getMyPersonas(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<AIPersonaDto> response = aiPersonaService.getPersonasByUserId(getCurrentUserId(), page, size);
        return ResponseEntity.ok(response);
    }

    /**
     * AI 페르소나 상세 조회
     *
     * @param personaId AI 페르소나 ID
     * @return AI 페르소나 DTO
     */
    @GetMapping("/{personaId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "AI 페르소나 상세 조회",
        description = "특정 AI 페르소나의 상세 정보를 조회합니다 (본인의 페르소나만 조회 가능)",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<AIPersonaDto> getPersonaById(@PathVariable Long personaId) {
        AIPersonaDto persona = aiPersonaService.getPersonaById(personaId, getCurrentUserId());
        return ResponseEntity.ok(persona);
    }

    /**
     * AI 페르소나 생성
     *
     * @param request AI 페르소나 생성 요청
     * @return 생성된 AI 페르소나 DTO
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "AI 페르소나 생성",
        description = "새로운 AI 페르소나를 생성합니다",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<AIPersonaDto> createPersona(@Valid @RequestBody AIPersonaRequest request) {
        AIPersonaDto createdPersona = aiPersonaService.createPersona(request, getCurrentUserId());
        return ResponseEntity.ok(createdPersona);
    }

    /**
     * AI 페르소나 삭제
     *
     * @param personaId AI 페르소나 ID
     * @return 응답 엔티티
     */
    @DeleteMapping("/{personaId}")
    @PreAuthorize("hasRole('USER')")
    @Operation(
        summary = "AI 페르소나 삭제",
        description = "AI 페르소나를 삭제합니다 (본인의 페르소나만 삭제 가능)",
        security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<Void> deletePersona(@PathVariable Long personaId) {
        aiPersonaService.deletePersona(personaId, getCurrentUserId());
        return ResponseEntity.noContent().build();
    }
}