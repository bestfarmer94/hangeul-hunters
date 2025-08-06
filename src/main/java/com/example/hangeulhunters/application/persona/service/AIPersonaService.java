package com.example.hangeulhunters.application.persona.service;

import com.example.hangeulhunters.application.persona.dto.AIPersonaDto;
import com.example.hangeulhunters.application.persona.dto.AIPersonaRequest;
import com.example.hangeulhunters.domain.persona.entity.AIPersona;
import com.example.hangeulhunters.domain.persona.repository.AIPersonaRepository;
import com.example.hangeulhunters.infrastructure.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AI 페르소나 서비스
 */
@Service
@RequiredArgsConstructor
public class AIPersonaService {

    private final AIPersonaRepository aiPersonaRepository;

    /**
     * 사용자의 AI 페르소나 목록 조회
     *
     * @param userId 사용자 ID
     * @return AI 페르소나 DTO 목록
     */
    @Transactional(readOnly = true)
    public List<AIPersonaDto> getPersonasByUserId(Long userId) {
        return aiPersonaRepository.findAllByUserIdAndDeletedAtNull(userId).stream()
                .map(AIPersonaDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * AI 페르소나 상세 조회
     *
     * @param id AI 페르소나 ID
     * @param userId 요청한 사용자 ID
     * @return AI 페르소나 DTO
     * @throws ResourceNotFoundException 페르소나가 존재하지 않는 경우
     * @throws AccessDeniedException 권한이 없는 경우
     */
    @Transactional(readOnly = true)
    public AIPersonaDto getPersonaById(Long id, Long userId) {
        // 사용자 본인의 페르소나만 조회 가능
        AIPersona persona = aiPersonaRepository.findByIdAndUserIdAndDeletedAtNull(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("AI Persona not found with id: " + id));
        
        return AIPersonaDto.fromEntity(persona);
    }

    /**
     * AI 페르소나 생성
     *
     * @param request AI 페르소나 생성 요청
     * @param userId 요청한 사용자 ID
     * @return 생성된 AI 페르소나 DTO
     * @throws ResourceNotFoundException 사용자가 존재하지 않는 경우
     */
    @Transactional
    public AIPersonaDto createPersona(AIPersonaRequest request, Long userId) {
        AIPersona persona = AIPersona.builder()
                .userId(userId)
                .name(request.getName())
                .gender(request.getGender())
                .age(request.getAge())
                .relationship(request.getRelationship())
                .description(request.getDescription())
                .profileImageUrl(request.getProfileImageUrl())
                .build();
        
        AIPersona savedPersona = aiPersonaRepository.save(persona);
        return AIPersonaDto.fromEntity(savedPersona);
    }

    /**
     * AI 페르소나 삭제 (소프트 딜리트)
     *
     * @param id AI 페르소나 ID
     * @param userId 요청한 사용자 ID
     * @throws ResourceNotFoundException 페르소나가 존재하지 않는 경우
     * @throws AccessDeniedException 권한이 없는 경우
     */
    @Transactional
    public void deletePersona(Long id, Long userId) {
        // 사용자 본인의 페르소나만 삭제 가능
        AIPersona persona = aiPersonaRepository.findByIdAndUserIdAndDeletedAtNull(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("AI Persona not found with id: " + id));
        
        persona.delete();
        aiPersonaRepository.save(persona);
    }
}