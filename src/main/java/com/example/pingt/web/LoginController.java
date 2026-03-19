package com.example.pingt.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    // ★ ここにあった @GetMapping("/register") を削除しました！
    // ユーザー登録の表示は UserController の方に任せるためです。
}