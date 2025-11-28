package com.example.hangeulhunters.domain.common.entity;

import com.example.hangeulhunters.domain.common.constant.FileObjectType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 파일 엔티티 (범용)
 * 대화(면접 등)와 관련된 파일 정보를 저장합니다.
 */
@Entity
@Table(name = "file")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class File extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileObjectType objectType;

    @Column(nullable = false)
    private Long objectId;

    @Column(nullable = false)
    private String fileUrl;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = true)
    private String fileType;

    @Column(nullable = true)
    private Long fileSize;
}
