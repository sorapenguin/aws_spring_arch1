package com.example.pingt.service;

import com.example.pingt.domain.PracticeRecord;
import com.example.pingt.domain.Question;
import com.example.pingt.domain.User;
import com.example.pingt.repository.PracticeRecordRepository;
import com.example.pingt.repository.QuestionRepository;
import com.example.pingt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PracticeService {

    private final PracticeRecordRepository recordRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    // もし習得レベルを管理するリポジトリが別にあるなら追加（例: QuestionStatusRepository）
    // なければ Question エンティティ自体にレベルを持たせているか、別途テーブルがあるはずです

    @Transactional
    public void recordAnswer(Long questionId, String username, boolean isCorrect) {
        // 1. ユーザーと問題を取得
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found: " + questionId));

        // 2. 履歴（Record）を作成・保存
        PracticeRecord record = PracticeRecord.builder()
                .user(user)
                .question(question)
                .correct(isCorrect)
                .answeredAt(LocalDateTime.now())
                .build();
        recordRepository.save(record);

        // 3. ★ 習得レベル（Mastery Level）の更新ロジックを追加
        updateMasteryLevel(user, question, isCorrect);
    }

    private void updateMasteryLevel(User user, Question question, boolean isCorrect) {
        // ここで「合格リーチ」などの判定を行います。
        // 一般的なロジック例：
        // ・不正解なら -> レベルを -1 (間違い) にする
        // ・正解 かつ 前が -1 なら -> レベルを 1 (合格リーチ) にする
        // ・正解 かつ 前が 1 なら -> レベルを 2 (習得済み) にする
        
        // ※ pingtプロジェクトの仕様に合わせて、Statusエンティティ等を更新する処理をここに記述します。
        // 例: 
        // QuestionStatus status = statusRepository.findByUserAndQuestion(user, question);
        // if (!isCorrect) {
        //     status.setMasteryLevel(-1);
        // } else if (status.getMasteryLevel() < 2) {
        //     status.setMasteryLevel(status.getMasteryLevel() + 1);
        // }
        // statusRepository.save(status);
    }
}