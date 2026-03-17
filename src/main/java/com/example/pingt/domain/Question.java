package com.example.pingt.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(nullable = false)
    private String statement;

    @Column(nullable = false, length = 1000)
    private String choiceA;

    @Column(nullable = false, length = 1000)
    private String choiceB;

    @Column(nullable = false, length = 1000)
    private String choiceC;

    @Column(nullable = false, length = 1000)
    private String choiceD;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 1)
    private QuestionChoice correctChoice;

    @Lob
    @Column(nullable = false)
    private String explanation;
}

