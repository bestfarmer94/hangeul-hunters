package com.example.hangeulhunters.application.user.service;

import com.example.hangeulhunters.application.auth.dto.SignUpRequest;
import com.example.hangeulhunters.application.file.service.FileService;
import com.example.hangeulhunters.application.interest.dto.InterestDto;
import com.example.hangeulhunters.application.interest.service.InterestService;
import com.example.hangeulhunters.application.user.dto.ProfileUpdateRequest;
import com.example.hangeulhunters.application.user.dto.UserDto;
import com.example.hangeulhunters.domain.common.constant.ImageType;
import com.example.hangeulhunters.domain.user.constant.UserRole;
import com.example.hangeulhunters.domain.user.entity.User;
import com.example.hangeulhunters.domain.user.repository.UserRepository;
import com.example.hangeulhunters.infrastructure.exception.ConflictException;
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
    private final FileService fileService;

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

    @Transactional
    public UserDto createUser(SignUpRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new ConflictException("Email is already taken");
        }

        // 프로필 이미지 처리
        String profileImageUrl = fileService.saveImageIfNeed(ImageType.USER_PROFILE, signUpRequest.getProfileImageUrl());
        
        // 사용자 생성 및 저장
        User user = User.builder()
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .nickname(signUpRequest.getNickname())
                .gender(signUpRequest.getGender())
                .birthDate(signUpRequest.getBirthDate())
                .profileImageUrl(profileImageUrl)
                .role(UserRole.ROLE_USER)
                .build();

        User savedUser = userRepository.save(user);
        return UserDto.fromEntity(savedUser);
    }
    
    @Transactional
    public UserDto updateProfile(Long userId, ProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        
        // 프로필 이미지 처리
        String profileImageUrl = fileService.saveImageIfNeed(ImageType.USER_PROFILE, request.getProfileImageUrl());

        // 기본 프로필 정보 업데이트
        user.updateProfile(
            request.getNickname(),
            request.getBirthDate(),
            request.getKoreanLevel(),
            profileImageUrl
        );
        
        // 관심사 업데이트
        if (request.getInterests() != null) {
            interestService.updateUserInterests(userId, request.getInterests());
        }
        
        User savedUser = userRepository.save(user);
        List<String> interests = getInterestsForUser(userId);
        return UserDto.fromEntity(savedUser, interests);
    }

    private List<String> getInterestsForUser(Long userId) {
        return interestService.getUserInterests(userId).stream()
                .map(InterestDto::getName)
                .collect(Collectors.toList());
    }
}