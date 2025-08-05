package com.example.hangeulhunters.domain.interest.entity;

import com.example.hangeulhunters.domain.common.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 관심사 엔티티
 */
@Entity
@Table(name = "interest")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Interest extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true)
    private Long userId;

    @Column(nullable = true)
    private Long personaId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String description;

}