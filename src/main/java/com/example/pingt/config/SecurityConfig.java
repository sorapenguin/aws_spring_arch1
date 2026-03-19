package com.example.pingt.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest; // ★追加
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // ★静的リソース（CSS, JS, 画像など）を認証の対象外にする
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                // ★ 1. H2 Console へのアクセスを許可
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/", "/questions", "/register", "/practice/**", "/css/**", "/js/**").permitAll()
                .anyRequest().authenticated()
            )
            // ★ 2. H2 Console のログイン（POST送信）でエラーにならないよう CSRF 対策を除外
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**")
            )
            // ★ 3. H2 Console をブラウザのフレーム内で表示できるように設定
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            )
// SecurityConfig.java の該当箇所

            .formLogin(login -> login
                .loginPage("/login")
                // 第2引数を true にすると、ログイン前のリクエストを無視して
                // 必ず指定したURL（ここでは "/"）に遷移させます
                .defaultSuccessUrl("/", true) 
                .permitAll()
            )
            .logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}