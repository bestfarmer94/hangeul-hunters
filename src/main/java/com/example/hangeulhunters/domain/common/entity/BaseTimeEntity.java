package com.example.hangeulhunters.domain.common.entity;

import com.example.hangeulhunters.infrastructure.util.DateTimeUtil;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class BaseTimeEntity {
    
    @CreatedDate
    @Column(updatable = false, nullable = false)
    private OffsetDateTime createdAt = DateTimeUtil.now();

    @LastModifiedDate
    @Column(nullable = true)
    private OffsetDateTime updatedAt = DateTimeUtil.now();

    @Column(nullable = true)
    private OffsetDateTime deletedAt;

    public void delete() {
        this.deletedAt = DateTimeUtil.now();
    }
}
