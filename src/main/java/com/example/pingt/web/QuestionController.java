package com.example.pingt.web;

import com.example.pingt.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequiredArgsConstructor
@Controller
public class QuestionController {
    private final QuestionService questionService;

    @GetMapping("/questions")
    public String list(Model model) {
        model.addAttribute("questions", questionService.listAll());
        return "questions/list";
    }

}

