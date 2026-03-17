package com.example.pingt.web;

import com.example.pingt.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@Controller
public class UserController {

    private final UserService userService;

    @GetMapping("/register")
    public String showForm(Model model) {
        model.addAttribute("form", new RegisterForm());
        return "user/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("form") RegisterForm form, Model model) {
        try {
            userService.register(form.getUsername(), form.getEmail(), form.getPassword());
            model.addAttribute("username", form.getUsername());
            return "user/register-complete";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "user/register";
        }
    }

    @Data
    public static class RegisterForm {
        private String username;
        private String email;
        private String password;
    }
}

