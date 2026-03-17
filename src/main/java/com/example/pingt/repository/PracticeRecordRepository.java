package com.example.pingt.repository;

import com.example.pingt.domain.PracticeRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PracticeRecordRepository extends JpaRepository<PracticeRecord, Long> {
}

