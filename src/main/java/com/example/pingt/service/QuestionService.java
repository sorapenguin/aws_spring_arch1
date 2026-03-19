package com.example.pingt.service;

import com.example.pingt.domain.Question;
import com.example.pingt.domain.User;
import com.example.pingt.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * 選択されたレベルと出題数に応じて、ランダムに問題IDを取得する
     */
    public List<Long> pickRandomQuestionIds(User user, List<Integer> levels, int limit) {
        // user.getId() を追加
        List<Question> questions = questionRepository.findRandomByMultipleLevels(
            user.getId(), 
            levels, 
            limit
        );

        return questions.stream()
                .map(Question::getId)
                .collect(Collectors.toList());
    }

    /**
     * 指定した習得レベル（-1, 0, 1, 2）の問題 ID をランダムに取得する
     */
    public List<Long> pickRandomIdsByLevel(User user, int level, int limit) {
        // user.getId() を追加
        return questionRepository.findRandomByMasteryLevel(user.getId(), level, limit)
                .stream()
                .map(Question::getId)
                .collect(Collectors.toList());
    }

    /**
     * 「2連続正解」以外のすべての問題 ID をランダムに取得する
     */
    public List<Long> pickRandomIdsNotMastered(User user, int limit) {
        // user.getId() を追加
        return questionRepository.findRandomNotMastered(user.getId(), limit)
                .stream()
                .map(Question::getId)
                .collect(Collectors.toList());
    }
    /**
     * PracticeControllerから呼ばれるメソッド。
     * 選択されたレベルに基づき、Questionオブジェクトのリストを返します。
     */
    public List<Question> getRandomQuestions(List<Integer> levels, int limit) {
        // 現在はUser情報を引数で受け取っていないため、全件からランダムに取得する暫定処理
        // (本来はSpring SecurityからUserを取得して絞り込みますが、まずはビルドを通します)
        
        List<Question> all = questionRepository.findAll();
        
        // TODO: ここで本来は引数の levels を使ってフィルタリングロジックを入れます
        
        java.util.Collections.shuffle(all);
        
        return all.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }
}