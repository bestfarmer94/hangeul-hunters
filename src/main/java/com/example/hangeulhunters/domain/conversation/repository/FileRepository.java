package com.example.hangeulhunters.domain.conversation.repository;

import com.example.hangeulhunters.domain.common.constant.FileObjectType;
import com.example.hangeulhunters.domain.common.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findAllByObjectTypeAndObjectIdAndDeletedAtNull(FileObjectType objectType, Long objectId);
}
