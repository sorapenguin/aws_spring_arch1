package com.example.pingt.repository;

import com.example.pingt.domain.QuestionStatus;
import com.example.pingt.domain.Question;
import com.example.pingt.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionStatusRepository extends JpaRepository<QuestionStatus, Long> {
    
    // --- 既存のメソッド ---
    List<QuestionStatus> findByUserUsername(String username);
    Optional<QuestionStatus> findByUserAndQuestion(User user, Question question);
    List<QuestionStatus> findByUser(User user);

    // --- 🚀 【追加】ユーザーごとの件数を集計するためのメソッド ---

    /**
     * 指定したユーザーがこれまでに回答（記録）した問題の総数を返します
     */
    long countByUser(User user);

    /**
     * 特定の習熟度（masteryLevel）に達している、そのユーザーの問題数を返します
     * 例：習熟度0（未着手）や 習熟度5（完璧）の数を数えるのに便利です
     */
    long countByUserAndMasteryLevel(User user, int masteryLevel);
}