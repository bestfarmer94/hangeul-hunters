package com.example.hangeulhunters.domain.user.entity;

import com.example.hangeulhunters.domain.common.constant.Gender;
import com.example.hangeulhunters.domain.common.entity.BaseTimeEntity;
import com.example.hangeulhunters.domain.user.constant.AuthProvider;
import com.example.hangeulhunters.domain.user.constant.KoreanLevel;
import com.example.hangeulhunters.domain.user.constant.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    @Column(nullable = true)
    private String providerId;

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private KoreanLevel koreanLevel;

    @Column(nullable = true)
    private String profileImageUrl;

    @Column(nullable = true)
    private String deviceId;

    @Builder.Default
    @Column(nullable = false)
    private Integer creditPoint = 0;

    public void updateProfile(Long userId, String nickname, LocalDate birthDate, KoreanLevel koreanLevel,
            String profileImageUrl) {
        super.update(userId);

        if (nickname != null) {
            this.nickname = nickname;
        }
        if (birthDate != null) {
            this.birthDate = birthDate;
        }
        if (koreanLevel != null) {
            this.koreanLevel = koreanLevel;
        }
        if (profileImageUrl != null) {
            this.profileImageUrl = profileImageUrl;
        }
    }

    public void chargeCreditPoint(Integer amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Charge amount must be positive");
        }
        this.creditPoint += amount;
    }

    public void deductCreditPoint(Integer amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deduct amount must be positive");
        }
        if (this.creditPoint < amount) {
            throw new com.example.hangeulhunters.infrastructure.exception.InsufficientCreditException(
                    "Insufficient credit points. Required: " + amount + ", Available: " + this.creditPoint);
        }
        this.creditPoint -= amount;
    }

    public boolean hasEnoughCredit(Integer amount) {
        return this.creditPoint >= amount;
    }
}