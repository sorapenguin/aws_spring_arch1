package com.example.pingt.config;

import com.example.pingt.domain.Question;
import com.example.pingt.domain.QuestionChoice;
import com.example.pingt.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DataInitializer implements CommandLineRunner {

    private final QuestionRepository questionRepository;

    @Override
    public void run(String... args) {
        if (questionRepository.count() > 0) {
            return;
        }

        questionRepository.save(Question.builder()
                .statement("Java のプリミティブ型でないものはどれ？")
                .choiceA("int")
                .choiceB("double")
                .choiceC("String")
                .choiceD("boolean")
                .correctChoice(QuestionChoice.C)
                .explanation("プリミティブ型は int/double/boolean など。String は参照型（クラス）です。")
                .build());

        questionRepository.save(Question.builder()
                .statement("次のうち、Java で継承に使うキーワードはどれ？")
                .choiceA("implements")
                .choiceB("extends")
                .choiceC("inherit")
                .choiceD("super")
                .correctChoice(QuestionChoice.B)
                .explanation("クラスの継承は extends、インタフェース実装は implements です。")
                .build());

        questionRepository.save(Question.builder()
                .statement("例外処理で、必ず実行されるブロックはどれ？")
                .choiceA("try")
                .choiceB("catch")
                .choiceC("throws")
                .choiceD("finally")
                .correctChoice(QuestionChoice.D)
                .explanation("finally ブロックは例外の有無に関わらず実行されます（JVM 強制終了などを除く）。")
                .build());
    }
}

