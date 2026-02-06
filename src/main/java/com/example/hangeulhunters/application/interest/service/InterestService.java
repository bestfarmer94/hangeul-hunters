package com.example.hangeulhunters.application.interest.service;

import com.example.hangeulhunters.application.interest.dto.InterestDto;
import com.example.hangeulhunters.domain.common.constant.SubjectCategory;
import com.example.hangeulhunters.domain.interest.entity.Interest;
import com.example.hangeulhunters.domain.interest.repository.InterestRepository;
import com.example.hangeulhunters.infrastructure.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 관심사 서비스
 */
@Service
@RequiredArgsConstructor
public class InterestService {

    private final InterestRepository interestRepository;

    /**
     * 사용자의 관심사 조회
     * 
     * @param userId 사용자 ID
     * @return 관심사 목록
     */
    @Transactional(readOnly = true)
    public List<InterestDto> getUserInterests(Long userId) {
        return interestRepository.findAllByUserIdAndDeletedAtNull(userId).stream()
                .map(InterestDto::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * 사용자의 관심사를 업데이트합니다.
     * 기존 관심사 중 요청에 없는 것은 삭제 처리하고, 새로운 관심사는 추가합니다.
     * 
     * @param userId 사용자 ID
     * @param interestNames 관심사 이름 목록
     */
    @Transactional
    public void updateUserInterests(Long userId, List<String> interestNames) {
        // 관심사 체크
         Set<String> subjects = Arrays.stream(SubjectCategory.values())
                .map(SubjectCategory::name)
                .collect(Collectors.toSet());

         if(interestNames.stream().anyMatch(interest -> !subjects.contains(interest))) {
             throw new BadRequestException("Invalid interest name in the request");
         }

        // 기존 관심사 조회
        List<Interest> existingInterests = interestRepository.findAllByUserIdAndDeletedAtNull(userId);
        
        // 이름으로 빠르게 조회할 수 있도록 맵 생성
        Map<String, Interest> existingInterestMap = new HashMap<>();
        for (Interest interest : existingInterests) {
            existingInterestMap.put(interest.getName(), interest);
        }
        
        // 요청된 관심사 처리
        List<Interest> interestsToSave = new ArrayList<>();
        
        for (String name : interestNames) {
            if (existingInterestMap.containsKey(name)) {
                // 기존 관심사는 맵에서 제거 (남은 것들은 삭제 대상)
                existingInterestMap.remove(name);
            } else {
                // 새로운 관심사 추가
                Interest newInterest = Interest.builder()
                        .userId(userId)
                        .name(name)
                        .createdBy(userId)
                        .build();
                interestsToSave.add(newInterest);
            }
        }
        
        // 새 관심사 저장
        if (!interestsToSave.isEmpty()) {
            interestRepository.saveAll(interestsToSave);
        }
        
        // 삭제 대상 관심사 처리
        for (Interest interest : existingInterestMap.values()) {
            interest.delete(userId);
            interestRepository.save(interest);
        }
    }
}