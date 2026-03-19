package com.example.pingt.repository;

import com.example.pingt.domain.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    Optional<Question> findFirstByIdGreaterThanOrderByIdAsc(Long id);
    Optional<Question> findFirstByOrderByIdAsc();

    /**
     * 指定したユーザーの、特定の習得レベルの問題をランダムに取得
     */
    @Query(value = "SELECT q.* FROM question q " +
           "LEFT JOIN question_statuses qs ON q.id = qs.question_id AND qs.user_id = :userId " +
           "WHERE COALESCE(qs.mastery_level, 0) = :level " +
           "ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Question> findRandomByMasteryLevel(@Param("userId") Long userId, @Param("level") int level, @Param("limit") int limit);

    /**
     * 指定したユーザーが「習得済み(2以上)」以外の問題をランダムに取得
     */
    @Query(value = "SELECT q.* FROM question q " +
           "LEFT JOIN question_statuses qs ON q.id = qs.question_id AND qs.user_id = :userId " +
           "WHERE COALESCE(qs.mastery_level, 0) < 2 " +
           "ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Question> findRandomNotMastered(@Param("userId") Long userId, @Param("limit") int limit);

    /**
     * 指定したユーザーの、複数の習得レベルで問題をランダムに検索
     */
    @Query(value = "SELECT q.* FROM question q " +
           "LEFT JOIN question_statuses qs ON q.id = qs.question_id AND qs.user_id = :userId " +
           "WHERE COALESCE(qs.mastery_level, 0) IN (:levels) " +
           "ORDER BY RAND() LIMIT :limit", nativeQuery = true)
    List<Question> findRandomByMultipleLevels(@Param("userId") Long userId, @Param("levels") List<Integer> levels, @Param("limit") int limit);
}