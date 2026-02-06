package com.example.hangeulhunters.domain.common.entity;

import com.example.hangeulhunters.infrastructure.util.DateTimeUtil;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;

@MappedSuperclass
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BaseTimeEntity {

    @Column(updatable = false, nullable = false)
    @Builder.Default
    private OffsetDateTime createdAt = DateTimeUtil.now();

    @Column(nullable = true)
    private OffsetDateTime updatedAt;

    @Column(nullable = true)
    private OffsetDateTime deletedAt;

    @Column(updatable = false, nullable = true)
    private Long createdBy;

    @Column(nullable = true)
    private Long updatedBy;
    
    @Column(nullable = true)
    private Long deletedBy;

    public void update(Long userId) {
        this.updatedAt = DateTimeUtil.now();
        this.updatedBy = userId;
    }

    public void delete(Long userId) {
        this.deletedAt = DateTimeUtil.now();
        this.deletedBy = userId;
    }

    public void restore() {
        this.deletedAt = null;
        this.deletedBy = null;
    }
}
