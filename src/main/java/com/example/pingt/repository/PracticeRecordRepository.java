package com.example.pingt.repository;

import com.example.pingt.domain.PracticeRecord;
import com.example.pingt.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PracticeRecordRepository extends JpaRepository<PracticeRecord, Long> {

    // 1. Userオブジェクトでの検索（既存）
    List<PracticeRecord> findByUserOrderByAnsweredAtDesc(User user);

    // ★ 追加：ユーザー名(String)で最新5件を取得（ProfileServiceのエラー解消用）
    List<PracticeRecord> findTop5ByUserUsernameOrderByAnsweredAtDesc(String username);

    // 2. 網羅率計算用（既存）
    @Query("SELECT COUNT(DISTINCT r.question.id) FROM PracticeRecord r " +
           "WHERE r.user = :user AND r.correct = true")
    long countMasteredQuestions(@Param("user") User user);
    
    // 3. 総回答数をカウント（既存）
    long countByUser(User user);

    // ★ 追加：ユーザー名(String)で総回答数をカウント（ProfileServiceのエラー解消用）
    long countByUserUsername(String username);
}