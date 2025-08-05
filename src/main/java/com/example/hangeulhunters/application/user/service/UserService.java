package com.example.hangeulhunters.application.user.service;

import com.example.hangeulhunters.application.auth.dto.SignUpRequest;
import com.example.hangeulhunters.application.interest.dto.InterestDto;
import com.example.hangeulhunters.application.interest.service.InterestService;
import com.example.hangeulhunters.application.user.dto.ProfileUpdateRequest;
import com.example.hangeulhunters.application.user.dto.UserDto;
import com.example.hangeulhunters.domain.user.constant.UserRole;
import com.example.hangeulhunters.domain.user.entity.User;
import com.example.hangeulhunters.domain.user.repository.UserRepository;
import com.example.hangeulhunters.infrastructure.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final InterestService interestService;

    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        List<String> interests = getInterestsForUser(id);
        return UserDto.fromEntity(user, interests);
    }

    @Transactional(readOnly = true)
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        List<String> interests = getInterestsForUser(user.getId());
        return UserDto.fromEntity(user, interests);
    }
    
    private List<String> getInterestsForUser(Long userId) {
        return interestService.getUserInterests(userId).stream()
                .map(InterestDto::getName)
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDto createUser(SignUpRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new IllegalArgumentException("Email is already taken");
        }

        User user = User.builder()
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .nickname(signUpRequest.getNickname())
                .birthDate(signUpRequest.getBirthDate())
                .role(UserRole.ROLE_USER)
                .build();

        User savedUser = userRepository.save(user);
        return UserDto.fromEntity(savedUser);
    }
    
    @Transactional
    public UserDto updateProfile(Long userId, ProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        // 기본 프로필 정보 업데이트
        user.updateProfile(
            request.getNickname(),
            request.getBirthDate(),
            request.getKoreanLevel(),
            request.getProfileImageUrl()
        );
        
        // 관심사 업데이트
        if (request.getInterests() != null && !request.getInterests().isEmpty()) {
            interestService.updateUserInterests(userId, request.getInterests());
        }
        
        User savedUser = userRepository.save(user);
        List<String> interests = getInterestsForUser(userId);
        return UserDto.fromEntity(savedUser, interests);
    }
}