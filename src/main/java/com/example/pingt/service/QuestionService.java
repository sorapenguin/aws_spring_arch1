package com.example.pingt.service;

import com.example.pingt.domain.Question;
import com.example.pingt.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class QuestionService {
    private final QuestionRepository questionRepository;

    public List<Question> listAll() {
        return questionRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    public Question getById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Question not found. id=" + id));
    }

    public Long nextQuestionIdOrFirst(Long currentId) {
        return questionRepository.findFirstByIdGreaterThanOrderByIdAsc(currentId)
                .or(() -> questionRepository.findFirstByOrderByIdAsc())
                .map(Question::getId)
                .orElseThrow(() -> new IllegalStateException("No questions exist"));
    }

    public List<Long> pickRandomQuestionIds(int limit) {
        List<Long> ids = questionRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
                .stream()
                .map(Question::getId)
                .collect(Collectors.toList());
        if (ids.isEmpty()) {
            throw new IllegalStateException("No questions exist");
        }
        Collections.shuffle(ids);
        if (limit >= ids.size()) {
            return ids;
        }
        return ids.subList(0, limit);
    }
}

