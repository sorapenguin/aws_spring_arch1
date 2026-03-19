package com.example.pingt.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "question_statuses")
public class QuestionStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ★追加：どのユーザーのステータスか
    @ManyToOne // 一人のユーザーが複数の問題ステータスを持つため
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // どの問題の状態か
    @ManyToOne // 修正：OneToOneだと全ユーザーで1問につき1つしか作れなくなるため、ManyToOneが一般的です
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    // 習得レベル
    // -1: 間違い
    //  0: 未出題
    //  1: 1回正解（合格リーチ）
    //  2: 2回連続正解（習得済み）
    @Column(nullable = false)
    private int masteryLevel;

    // 最後に解いた結果
    private boolean lastResult;
}