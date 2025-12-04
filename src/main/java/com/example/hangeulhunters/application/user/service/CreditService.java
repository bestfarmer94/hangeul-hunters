package com.example.hangeulhunters.application.user.service;

import com.example.hangeulhunters.application.user.dto.UserDto;
import com.example.hangeulhunters.domain.user.entity.User;
import com.example.hangeulhunters.domain.user.repository.UserRepository;
import com.example.hangeulhunters.infrastructure.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditService {

    private final UserRepository userRepository;

    /**
     * Charge credit points to user
     */
    @Transactional
    public UserDto chargeCredit(Long userId, Integer amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        user.chargeCreditPoint(amount);
        userRepository.save(user);

        log.info("Charged {} points to user {}, new balance: {}",
                amount, userId, user.getCreditPoint());

        return UserDto.fromEntity(user);
    }

    /**
     * Deduct credit points from user
     */
    @Transactional
    public UserDto deductCredit(Long userId, Integer amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        user.deductCreditPoint(amount); // throws InsufficientCreditException
        userRepository.save(user);

        log.info("Deducted {} points from user {}, new balance: {}",
                amount, userId, user.getCreditPoint());

        return UserDto.fromEntity(user);
    }

    /**
     * Get user's current credit balance
     */
    @Transactional(readOnly = true)
    public Integer getCreditBalance(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        return user.getCreditPoint();
    }
}
