package com.example.hangeulhunters.application.persona.service;

import com.example.hangeulhunters.application.common.dto.PageResponse;
import com.example.hangeulhunters.application.persona.dto.AIPersonaDto;
import com.example.hangeulhunters.application.persona.dto.AIPersonaRequest;
import com.example.hangeulhunters.domain.persona.constant.PersonaVoice;
import com.example.hangeulhunters.domain.persona.entity.AIPersona;
import com.example.hangeulhunters.domain.persona.repository.AIPersonaRepository;
import com.example.hangeulhunters.infrastructure.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * AI 페르소나 서비스
 */
@Service
@RequiredArgsConstructor
public class AIPersonaService {

    private final AIPersonaRepository aiPersonaRepository;

    /**
     * 사용자의 AI 페르소나 목록 페이징 조회
     *
     * @param userId 사용자 ID
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return AI 페르소나 페이지 응답 DTO
     */
    @Transactional(readOnly = true)
    public PageResponse<AIPersonaDto> getPersonasByUserId(Long userId, int page, int size) {
        // 생성일 기준 내림차순 정렬 (최신순)
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        Page<AIPersona> personaPage = aiPersonaRepository.findAllByUserIdAndDeletedAtNull(userId, pageable);
        
        return PageResponse.of(personaPage, AIPersonaDto::fromEntity);
    }

    /**
     * AI 페르소나 상세 조회
     *
     * @param userId 요청한 사용자 ID
     * @param personaId AI 페르소나 ID
     * @return AI 페르소나 DTO
     * @throws ResourceNotFoundException 페르소나가 존재하지 않는 경우
     * @throws AccessDeniedException 권한이 없는 경우
     */
    @Transactional(readOnly = true)
    public AIPersonaDto getPersonaById(Long userId, Long personaId) {
        // 사용자 본인의 페르소나만 조회 가능
        AIPersona persona = aiPersonaRepository.findByIdAndUserIdAndDeletedAtNull(personaId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("AI Persona not found with id: " + personaId));
        
        return AIPersonaDto.fromEntity(persona);
    }

    /**
     * AI 페르소나 상세 조회 (삭제 포함)
     *
     * @param personaId AI 페르소나 ID
     * @return AI 페르소나 DTO
     * @throws ResourceNotFoundException 페르소나가 존재하지 않는 경우
     * @throws AccessDeniedException 권한이 없는 경우
     */
    @Transactional(readOnly = true)
    public AIPersonaDto getPersonaByIdIncludeDeleted(Long personaId) {
        // 사용자 본인의 페르소나만 조회 가능
        AIPersona persona = aiPersonaRepository.findById(personaId)
                .orElseThrow(() -> new ResourceNotFoundException("AI Persona not found with id: " + personaId));

        return AIPersonaDto.fromEntity(persona);
    }

    /**
     * AI 페르소나 생성
     *
     * @param userId 요청한 사용자 ID
     * @param request AI 페르소나 생성 요청
     * @return 생성된 AI 페르소나 DTO
     * @throws ResourceNotFoundException 사용자가 존재하지 않는 경우
     */
    @Transactional
    public AIPersonaDto createPersona(Long userId, AIPersonaRequest request) {

        AIPersona persona = AIPersona.builder()
                .userId(userId)
                .name(request.getName())
                .gender(request.getGender())
                .age(request.getAge())
                .aiRole(request.getName())
                .userRole(request.getUserRole())
                .description(request.getDescription())
                .profileImageUrl(request.getProfileImageUrl())
                .voice(PersonaVoice.LEDA.getVoiceName())
                .createdBy(userId)
                .build();
        
        AIPersona savedPersona = aiPersonaRepository.save(persona);
        return AIPersonaDto.fromEntity(savedPersona);
    }

    /**
     * AI 페르소나 삭제 (소프트 딜리트)
     *
     * @param userId 요청한 사용자 ID
     * @param personaId AI 페르소나 ID
     * @throws ResourceNotFoundException 페르소나가 존재하지 않는 경우
     * @throws AccessDeniedException 권한이 없는 경우
     */
    @Transactional
    public void deletePersona(Long userId, Long personaId) {
        // 사용자 본인의 페르소나만 삭제 가능
        AIPersona persona = aiPersonaRepository.findByIdAndUserIdAndDeletedAtNull(personaId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("AI Persona not found with id: " + personaId));
        
        persona.delete(userId);
        aiPersonaRepository.save(persona);
    }
}