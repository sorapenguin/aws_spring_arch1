package com.example.pingt.service;

import com.example.pingt.domain.User;
import com.example.pingt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder; // ★追加
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // ★追加（SecurityConfigで定義したもの）

    public User register(String username, String email, String rawPassword) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("既に使用されているユーザー名です。");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("既に使用されているメールアドレスです。");
        }

        // ★重要：パスワードをハッシュ化（暗号化）して保存する
        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(rawPassword)) // ★ここを修正！
                .role("USER") // Roleは "USER" でOK（Springが自動で ROLE_USER として扱います）
                .enabled(true)
                .build();
        
        return userRepository.save(user);
    }
}