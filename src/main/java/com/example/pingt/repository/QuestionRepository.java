package com.example.pingt.repository;

import com.example.pingt.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    Optional<Question> findFirstByIdGreaterThanOrderByIdAsc(Long id);
    Optional<Question> findFirstByOrderByIdAsc();
}

