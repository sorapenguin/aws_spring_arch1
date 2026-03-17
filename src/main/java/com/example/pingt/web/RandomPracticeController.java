package com.example.pingt.web;

import com.example.pingt.service.QuestionService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/practice/random")
public class RandomPracticeController {

    private static final String SESSION_KEY_IDS = "RANDOM_QUESTION_IDS";
    private static final String SESSION_KEY_INDEX = "RANDOM_QUESTION_INDEX";

    private final QuestionService questionService;

    @GetMapping("/start")
    public String start(HttpSession session) {
        List<Long> ids = questionService.pickRandomQuestionIds(10);
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

