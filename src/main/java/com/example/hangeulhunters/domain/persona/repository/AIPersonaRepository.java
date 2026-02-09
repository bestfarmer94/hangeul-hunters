package com.example.hangeulhunters.domain.persona.repository;

import com.example.hangeulhunters.domain.persona.entity.AIPersona;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * AI 페르소나 저장소
 */
@Repository
public interface AIPersonaRepository extends JpaRepository<AIPersona, Long> {

    
    /**
     * 사용자 ID로 AI 페르소나 목록 페이징 조회 (삭제되지 않은 것만)
     *
     * @param userId 사용자 ID
     * @param pageable 페이징 정보
     * @return 페이징된 AI 페르소나 목록
     */
    Page<AIPersona> findAllByUserIdAndDeletedAtNull(Long userId, Pageable pageable);

    Optional<AIPersona> findByIdAndDeletedAtNull(Long id);
}