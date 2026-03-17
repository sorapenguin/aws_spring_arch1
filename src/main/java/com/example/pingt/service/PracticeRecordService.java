package com.example.pingt.service;

import com.example.pingt.domain.PracticeRecord;
import com.example.pingt.domain.Question;
import com.example.pingt.repository.PracticeRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
@Transactional
public class PracticeRecordService {
    private final PracticeRecordRepository repository;

    public void record(Question question, LocalDateTime answeredAt, boolean correct) {
        PracticeRecord record = PracticeRecord.builder()
                .question(question)
                .user(null) // TODO: 将来的にログインユーザーを紐付け
                .answeredAt(answeredAt)
                .durationMillis(0L)
                .correct(correct)
                .build();
        repository.save(record);
    }
}

