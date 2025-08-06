package com.example.hangeulhunters.domain.persona.repository;

import com.example.hangeulhunters.domain.persona.entity.AIPersona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * AI 페르소나 저장소
 */
@Repository
public interface AIPersonaRepository extends JpaRepository<AIPersona, Long> {

    List<AIPersona> findAllByUserIdAndDeletedAtNull(Long userId);

    Optional<AIPersona> findByIdAndUserIdAndDeletedAtNull(Long id, Long userId);
}