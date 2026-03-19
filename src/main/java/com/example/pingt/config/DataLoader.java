package com.example.pingt.config;

import com.example.pingt.domain.User;
import com.example.pingt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 1. テストユーザー "guest" が存在するか確認
        if (userRepository.findByUsername("guest").isEmpty()) {
            
            // 2. 存在しなければ作成して保存
            User guestUser = User.builder()
                    .username("guest")
                    .email("guest@example.com")
                    .password(passwordEncoder.encode("password")) // パスワードは "password"
                    .role("USER")
                    .enabled(true)
                    .build();

            userRepository.save(guestUser);
            System.out.println("✅ テストユーザー 'guest' を作成しました。");
        }
    }
}