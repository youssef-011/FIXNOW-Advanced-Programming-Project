package com.fix.fixnow.controller;

<<<<<<< HEAD
import org.springframework.stereotype.Controller;
=======
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
>>>>>>> 807d0dc (fix mvc view controllers routing)
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthViewController {

    @GetMapping("/login")
<<<<<<< HEAD
    public String loginPage() {
=======
    public String loginPage(Model model) {
        model.addAttribute("pageTitle", "Login - FixNow");
>>>>>>> 807d0dc (fix mvc view controllers routing)
        return "login";
    }

    @GetMapping("/register")
<<<<<<< HEAD
    public String registerPage() {
        return "register";
    }
=======
    public String registerPage(Model model) {
        model.addAttribute("pageTitle", "Register - FixNow");
        return "register";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
>>>>>>> 807d0dc (fix mvc view controllers routing)
}