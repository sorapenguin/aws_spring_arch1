package com.example.pingt.service;

import com.example.pingt.domain.PracticeRecord;
import com.example.pingt.domain.Question;
import com.example.pingt.domain.User; // ★これが必要
import com.example.pingt.domain.QuestionStatus;
import com.example.pingt.repository.PracticeRecordRepository;
import com.example.pingt.repository.QuestionStatusRepository;
import com.example.pingt.repository.UserRepository; // ★これが必要
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
@Transactional // クラス全体に付ける
public class PracticeRecordService {
    private final PracticeRecordRepository repository;
    private final QuestionStatusRepository questionStatusRepository;
    private final UserRepository userRepository;

    public void record(Question question, String username, LocalDateTime answeredAt, boolean correct) {
        System.out.println("DEBUG: recordメソッド開始");
        
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("ユーザーが見つかりません: " + username));

            // 1. 解答履歴の保存
            PracticeRecord record = PracticeRecord.builder()
                    .question(question)
                    .user(user)
                    .answeredAt(answeredAt)
                    .durationMillis(0L)
                    .correct(correct)
                    .build();
            
            repository.saveAndFlush(record); // Flushで即時反映
            System.out.println("DEBUG: PracticeRecord保存完了 ID=" + record.getId());

            // 2. 習得レベルの更新
            updateMasteryLevel(question, user, correct);
            
            System.out.println("DEBUG: recordメソッド正常終了");

        } catch (Exception e) {
            System.err.println("DEBUG: 保存中にエラーが発生しました！");
            e.printStackTrace(); // ここに赤い文字で原因が出るはずです
            throw e; // エラーを投げてロールバックさせる
        }
    }

    private void updateMasteryLevel(Question question, User user, boolean correct) {
        QuestionStatus status = questionStatusRepository.findByUserAndQuestion(user, question)
                .orElseGet(() -> {
                    System.out.println("DEBUG: 新規Status作成");
                    return QuestionStatus.builder()
                            .user(user).question(question).masteryLevel(0).build();
                });

        if (correct) {
            if (status.getMasteryLevel() <= 0) {
                status.setMasteryLevel(1);
            } else if (status.getMasteryLevel() == 1) {
                status.setMasteryLevel(2);
            }
        } else {
            status.setMasteryLevel(-1);
        }

        status.setLastResult(correct);
        
        // ★ここも saveAndFlush に変更
        questionStatusRepository.saveAndFlush(status);
        System.out.println("DEBUG: updateMasteryLevel完了 新Level=" + status.getMasteryLevel());
    }

}