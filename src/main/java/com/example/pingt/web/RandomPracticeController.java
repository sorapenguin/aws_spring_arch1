package com.example.pingt.web;

import com.example.pingt.domain.User;
import com.example.pingt.repository.UserRepository; // ★追加
import com.example.pingt.service.QuestionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal; // ★追加
import org.springframework.security.core.userdetails.UserDetails; // ★追加
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/practice/random")
public class RandomPracticeController {

    private static final String SESSION_KEY_IDS = "RANDOM_QUESTION_IDS";
    private static final String SESSION_KEY_INDEX = "RANDOM_QUESTION_INDEX";

    private final QuestionService questionService;
    private final UserRepository userRepository; // ★追加：ユーザーをDBから取得するために必要

    @GetMapping("/start")
    public String start(
            @AuthenticationPrincipal UserDetails userDetails, // ★追加：ログインユーザー情報の取得
            @RequestParam(name = "levels", required = false) List<Integer> levels,
            @RequestParam(name = "limit", defaultValue = "10") String limitStr,
            HttpSession session) {
        
        // 1. レベルの判定
        if (levels == null || levels.isEmpty()) {
            levels = List.of(0, -1, 1);
        }

        // 2. 出題数の判定
        int limit = "all".equals(limitStr) ? Integer.MAX_VALUE : Integer.parseInt(limitStr);

        // 3. ログイン中のUserエンティティを取得してServiceに渡す
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ★修正点：第一引数に user を追加
        List<Long> ids = questionService.pickRandomQuestionIds(user, levels, limit);

        // 4. 問題が1件もなかったら一覧へ
        if (ids.isEmpty()) {
            return "redirect:/questions";
        }

        session.setAttribute(SESSION_KEY_IDS, ids);
        session.setAttribute(SESSION_KEY_INDEX, 0);

        return "redirect:/practice/random/question";
    }

    @GetMapping("/question")
    public String current(HttpSession session) {
        @SuppressWarnings("unchecked")
        List<Long> ids = (List<Long>) session.getAttribute(SESSION_KEY_IDS);
        Integer index = (Integer) session.getAttribute(SESSION_KEY_INDEX);
        if (ids == null || index == null || index < 0 || index >= ids.size()) {
            return "redirect:/questions";
        }
        Long currentId = ids.get(index);
        return "redirect:/practice/" + currentId;
    }

    @GetMapping("/next")
    public String next(HttpSession session) {
        advanceToNext(session);
        return "redirect:/practice/random/question";
    }

    public static void advanceToNext(HttpSession session) {
        @SuppressWarnings("unchecked")
        List<Long> ids = (List<Long>) session.getAttribute(SESSION_KEY_IDS);
        Integer index = (Integer) session.getAttribute(SESSION_KEY_INDEX);
        if (ids == null || index == null) {
            return;
        }
        int next = index + 1;
        if (next >= ids.size()) {
            session.removeAttribute(SESSION_KEY_IDS);
            session.removeAttribute(SESSION_KEY_INDEX);
        } else {
            session.setAttribute(SESSION_KEY_INDEX, next);
        }
    }
}