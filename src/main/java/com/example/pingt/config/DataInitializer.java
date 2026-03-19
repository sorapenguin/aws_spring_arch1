package com.example.pingt.config;

import com.example.pingt.domain.Question;
import com.example.pingt.domain.QuestionChoice;
import com.example.pingt.domain.User;
import com.example.pingt.repository.QuestionRepository;
import com.example.pingt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DataInitializer implements CommandLineRunner {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        try { // ★ try の開始
            // 1. ユーザーの登録チェック
            boolean nameExists = userRepository.findByUsername("guest2").isPresent();
            boolean emailExists = userRepository.findByEmail("guest2@example.com").isPresent();

            if (!nameExists && !emailExists) {
                User guest = User.builder()
                        .username("guest2")
                        .email("guest2@example.com")
                        .password(passwordEncoder.encode("password123")) 
                        .enabled(true)
                        .role("USER") 
                        .build();
                
                userRepository.save(guest);
                System.out.println("✅ DEBUG: guest2 ユーザーを新規作成しました");
            } else {
                System.out.println("ℹ️ DEBUG: guest2 または guest@example.com は既に存在するため作成をスキップしました");
            }

            // 2. 問題データの登録
            if (questionRepository.count() == 0) {
                questionRepository.save(Question.builder()
                        .statement("Java のプリミティブ型でないものはどれ？")
                        .choiceA("int").choiceB("double").choiceC("String").choiceD("boolean")
                        .correctChoice(QuestionChoice.C)
                        .explanation("プリミティブ型は int/double/boolean など。String は参照型（クラス）です。")
                        .build());

                questionRepository.save(Question.builder()
                        .statement("次のうち、Java で継承に使うキーワードはどれ？")
                        .choiceA("implements").choiceB("extends").choiceC("inherit").choiceD("super")
                        .correctChoice(QuestionChoice.B)
                        .explanation("クラスの継承は extends です。")
                        .build());

                questionRepository.save(Question.builder()
                        .statement("例外処理で、必ず実行されるブロックはどれ？")
                        .choiceA("try").choiceB("catch").choiceC("throws").choiceD("finally")
                        .correctChoice(QuestionChoice.D)
                        .explanation("finally ブロックは例外の有無に関わらず実行されます。")
                        .build());
                System.out.println("✅ DEBUG: 初期問題を登録しました。");
            }
        } catch (Exception e) { // ★ try を閉じて catch を追加
            System.err.println("❌ DEBUG: 初期化中にエラーが発生しました: " + e.getMessage());
            e.printStackTrace();
        }
    } // run メソッドの終わり
} // クラスの終わり