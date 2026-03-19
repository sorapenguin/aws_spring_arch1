package com.example.pingt.web;

import com.example.pingt.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult; // ★追加
import org.springframework.validation.annotation.Validated; // ★追加
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@RequiredArgsConstructor
@Controller
public class UserController {

    private final UserService userService;

    @GetMapping("/register")
    public String showForm(Model model) {
        // フォームが空の状態で画面を表示
        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new RegisterForm());
        }
        return "register"; // パスは templates 直下の場合
    }

    @PostMapping("/register")
    public String register(
            @Validated @ModelAttribute("form") RegisterForm form, // ★ @Validatedを追加
            BindingResult bindingResult, // ★ エラー結果を受け取る
            Model model,
            RedirectAttributes redirectAttributes) {

        // 1. 入力チェック（型や長さの違反）があれば入力画面に戻す
        if (bindingResult.hasErrors()) {
            return "register";
        }

        try {
            userService.register(form.getUsername(), form.getEmail(), form.getPassword());
            
            // 2. 成功時：リダイレクトでログイン画面へ（二重送信防止！）
            redirectAttributes.addFlashAttribute("successMessage", "登録が完了しました。ログインしてください。");
            return "redirect:/login"; 
            
        } catch (IllegalArgumentException ex) {
            // 3. 業務エラー（ユーザー名重複など）があればメッセージを付けて戻す
            model.addAttribute("errorMessage", ex.getMessage());
            return "register";
        }
    }

    @Data
    public static class RegisterForm {
        @NotBlank(message = "ユーザー名は必須です")
        @Size(min = 3, max = 20, message = "ユーザー名は3文字以上20文字以内で入力してください")
        private String username;

        @NotBlank(message = "メールアドレスは必須です")
        @Email(message = "メールアドレスの形式が正しくありません")
        private String email;

        @NotBlank(message = "パスワードは必須です")
        @Size(min = 8, message = "パスワードは8文字以上で入力してください")
        private String password;
    }
}