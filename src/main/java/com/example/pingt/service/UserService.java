package com.example.pingt.service;

import com.example.pingt.domain.User;
import com.example.pingt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    public User register(String username, String email, String rawPassword) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("既に使用されているユーザー名です。");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("既に使用されているメールアドレスです。");
        }
        // 簡易実装: パスワードはそのまま保存（本番ではハッシュ化が必須）
        User user = User.builder()
                .username(username)
                .email(email)
                .password(rawPassword)
                .role("ROLE_USER")
                .enabled(true)
                .build();
        return userRepository.save(user);
    }
}

