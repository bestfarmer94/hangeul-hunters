package com.example.hangeulhunters.domain.interest.repository;

import com.example.hangeulhunters.domain.interest.entity.Interest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterestRepository extends JpaRepository<Interest, Long> {

    List<Interest> findAllByUserIdAndDeletedAtNull(Long userId);

}