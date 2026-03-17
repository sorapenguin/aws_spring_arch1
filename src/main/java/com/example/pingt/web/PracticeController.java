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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/practice")
public class PracticeController {
    private final QuestionService questionService;
    private final PracticeRecordService practiceRecordService;

    @PostMapping("/{id}/answer")
    public String answer(@PathVariable("id") Long id, AnswerForm form, Model model, HttpSession session) {
        Question question = questionService.getById(id);
        QuestionChoice selected = form.getSelectedChoice();

        boolean correct = selected != null && selected == question.getCorrectChoice();
        java.time.LocalDateTime answeredAt = java.time.LocalDateTime.now();
        practiceRecordService.record(question, answeredAt, correct);
        Long nextId = questionService.nextQuestionIdOrFirst(id);
        RandomPracticeController.advanceToNext(session);

        model.addAttribute("question", question);
        model.addAttribute("selectedChoice", selected);
        model.addAttribute("correct", correct);
        model.addAttribute("answeredAt", answeredAt);
        model.addAttribute("nextId", nextId);
        return "practice/result";
    }

    @Data
    public static class AnswerForm {
        private QuestionChoice selectedChoice;
    }
}

