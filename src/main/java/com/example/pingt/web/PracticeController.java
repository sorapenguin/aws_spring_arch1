package com.example.pingt.web;

import com.example.pingt.domain.Question;
import com.example.pingt.domain.QuestionChoice;
import com.example.pingt.service.PracticeRecordService;
import com.example.pingt.service.QuestionService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
/**
 * 演習回答（個別およびランダム）を制御するコントローラー
 */
@RequiredArgsConstructor
@Controller
@RequestMapping("/practice")
public class PracticeController {

    private final QuestionService questionService;
    private final PracticeRecordService practiceRecordService;

    /**
     * ランダム演習開始：条件に合う問題をセッションに詰めて、最初の問題へリダイレクト
     */
    @GetMapping("/random/start")
    public String startRandom(
            @RequestParam(value = "limit", defaultValue = "5") int limit,
            @RequestParam(value = "levels", required = false) java.util.List<Integer> levels,
            HttpSession session) {
        
        // 1. 条件に合う問題をランダムに取得（Service側で実装が必要）
        java.util.List<Question> questions = questionService.getRandomQuestions(levels, limit);
        
        // 2. 問題のIDリストと現在の位置(0)をセッションに保存
        java.util.List<Long> ids = questions.stream().map(Question::getId).toList();
        session.setAttribute("practiceIds", ids);
        session.setAttribute("currentIndex", 0);

        if (ids.isEmpty()) return "redirect:/";
        
        // 最初の問題へ
        return "redirect:/practice/random/next";
    }

    /**
     * 次の問題を取得して表示する
     */
    @GetMapping("/random/next")
    public String nextRandom(HttpSession session, Model model) {
        java.util.List<Long> ids = (java.util.List<Long>) session.getAttribute("practiceIds");
        Integer currentIndex = (Integer) session.getAttribute("currentIndex");

        if (ids == null || currentIndex == null || currentIndex >= ids.size()) {
            return "redirect:/practice/final-result";
        }

        Long currentId = ids.get(currentIndex);
        Question question = questionService.getById(currentId);
        
        // ★ ここが重要：JSに「次のID」を教える（なければnull）
        Long nextId = (currentIndex + 1 < ids.size()) ? ids.get(currentIndex + 1) : null;

        model.addAttribute("question", question);
        model.addAttribute("nextQuestionId", nextId); // HTMLのJSで使用

        return "practice/solve";
    }

    /**
     * 解答判定
     */
    @PostMapping("/{id}/answer")
    @ResponseBody
    public Map<String, Object> answer(
            @PathVariable("id") Long id,
            AnswerForm form, 
            HttpSession session, // セッション追加
            Principal principal) {

        Map<String, Object> response = new HashMap<>();
        try {
            Question question = questionService.getById(id);
            boolean isCorrect = (form.getSelectedChoice() != null && form.getSelectedChoice().equals(question.getCorrectChoice()));

            if (principal != null) {
                practiceRecordService.record(question, principal.getName(), LocalDateTime.now(), isCorrect);
            }

            // ★ 解答が届いたので、セッションの「現在地」を1つ進める
            Integer currentIndex = (Integer) session.getAttribute("currentIndex");
            if (currentIndex != null) {
                session.setAttribute("currentIndex", currentIndex + 1);
            }

            response.put("isCorrect", isCorrect);
            response.put("status", "success");

        } catch (Exception e) {
            response.put("status", "error");
        }
        return response;
    }

    @GetMapping("/final-result")
    public String showFinalResult() {
        return "practice/final-result";
    }

    @Data
    public static class AnswerForm {
        private QuestionChoice selectedChoice;
    }
}