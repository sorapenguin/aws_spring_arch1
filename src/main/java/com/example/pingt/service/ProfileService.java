package com.example.pingt.service;

import com.example.pingt.domain.QuestionStatus;
import com.example.pingt.repository.PracticeRecordRepository;
import com.example.pingt.repository.QuestionRepository;
import com.example.pingt.repository.QuestionStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final PracticeRecordRepository practiceRecordRepository;
    private final QuestionRepository questionRepository;
    private final QuestionStatusRepository questionStatusRepository;

    public Map<String, Object> getUserStats(String username) {
        Map<String, Object> stats = new HashMap<>();

        // 1. ユーザーの全進捗データを取得
        List<QuestionStatus> allStatuses = questionStatusRepository.findByUserUsername(username);
        long totalQuestions = questionRepository.count();

        // 2. ★ここで「正解(1)」と「習得(2以上)」を別々にカウントする
        long level1Count = allStatuses.stream()
                .filter(s -> s.getMasteryLevel() == 1)
                .count();

        long masteredCount = allStatuses.stream()
                .filter(s -> s.getMasteryLevel() >= 2)
                .count();

        // 3. 総回答数（延べ回数）
        long totalAnswers = practiceRecordRepository.countByUserUsername(username);

        // 4. 最近の履歴
        var recentRecords = practiceRecordRepository.findTop5ByUserUsernameOrderByAnsweredAtDesc(username);

        // 5. ★ 全てのデータをMapに詰め込む（HTML側で使う名前と一致させる）
        stats.put("totalQuestions", totalQuestions);
        stats.put("totalAnswers", totalAnswers);
        stats.put("masteredCount", masteredCount);   // 濃い青用
        stats.put("level1Count", level1Count);       // 薄い青用
        stats.put("recentRecords", recentRecords);
        
        // 網羅率の計算（安全のため）
        long progressRate = totalQuestions > 0 
                ? (masteredCount + level1Count) * 100 / totalQuestions 
                : 0;
        stats.put("progressRate", progressRate);

        return stats;
    }
}