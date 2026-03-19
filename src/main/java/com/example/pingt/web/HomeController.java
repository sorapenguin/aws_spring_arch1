package com.example.pingt.web;

import com.example.pingt.domain.Question;
import com.example.pingt.domain.QuestionStatus;
import com.example.pingt.repository.QuestionRepository;
import com.example.pingt.repository.QuestionStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequiredArgsConstructor
public class HomeController {

    private final QuestionRepository questionRepository;
    private final QuestionStatusRepository questionStatusRepository;

@GetMapping("/")
public String index(Model model, java.security.Principal principal) {
    List<Question> allQuestions = questionRepository.findAll();
    
    // ★ 修正：全履歴ではなく、ログインユーザーの履歴だけを取得する
    List<QuestionStatus> userStatuses = new ArrayList<>();
    if (principal != null) {
        // Repositoryに作った findByUserUsername を使用
        userStatuses = questionStatusRepository.findByUserUsername(principal.getName());
    }

    List<Map<String, Object>> displayStatusList = new ArrayList<>();
    long level1Count = 0;
    long masteredCount = 0;

    if (allQuestions != null && !allQuestions.isEmpty()) {
        for (Question q : allQuestions) {
            Map<String, Object> data = new HashMap<>();
            Map<String, Object> questionMap = new HashMap<>();
            questionMap.put("id", q.getId());
            data.put("question", questionMap);
            
            int level = 0; 
            // ★ 修正：ログインユーザーの履歴(userStatuses)の中から、この問題のものを探す
            for (QuestionStatus s : userStatuses) {
                if (s.getQuestion() != null && s.getQuestion().getId().equals(q.getId())) {
                    level = s.getMasteryLevel();
                    break;
                }
            }
            
            if (level == 1) {
                level1Count++;
            } else if (level >= 2) {
                masteredCount++;
            }
            
            data.put("masteryLevel", level);
            displayStatusList.add(data);
        }
    }

    model.addAttribute("totalCount", allQuestions != null ? allQuestions.size() : 0);
    model.addAttribute("questionStatusList", displayStatusList); 
    model.addAttribute("level1Count", level1Count);
    model.addAttribute("masteredCount", masteredCount);
    
    return "index";
}
}