package com.fix.fixnow.controller;

import com.fix.fixnow.dto.LoginDTO;
import com.fix.fixnow.dto.RegisterDTO;
import com.fix.fixnow.model.User;
import com.fix.fixnow.security.SessionAuthConstants;
import com.fix.fixnow.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterDTO registerDTO, BindingResult bindingResult, HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "redirect:/register?error";
        }

        User user = new User();
        user.setName(registerDTO.getName());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(registerDTO.getPassword());
        user.setPhone(registerDTO.getPhone());
        user.setRole(registerDTO.getRole());

        try {
            User savedUser = authService.register(user);
            storeUserInSession(session, savedUser);
            return redirectForRole(savedUser);
        } catch (RuntimeException ex) {
            return "redirect:/register?error";
        }
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginDTO loginDTO, BindingResult bindingResult, HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "redirect:/login?error";
        }

        return authService.login(loginDTO.getEmail(), loginDTO.getPassword())
                .map(user -> {
                    storeUserInSession(session, user);
                    return redirectForRole(user);
                })
                .orElse("redirect:/login?error");
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    private String redirectForRole(User user) {
        return switch (user.getRole()) {
            case CUSTOMER -> "redirect:/customer/dashboard";
            case TECHNICIAN -> "redirect:/technician/dashboard";
            case ADMIN -> "redirect:/admin/dashboard";
        };
    }

    private void storeUserInSession(HttpSession session, User user) {
        session.setAttribute(SessionAuthConstants.AUTH_USER_ID, user.getId());
        session.setAttribute(SessionAuthConstants.AUTH_NAME, user.getName());
        session.setAttribute(SessionAuthConstants.AUTH_EMAIL, user.getEmail());
        session.setAttribute(SessionAuthConstants.AUTH_ROLE, user.getRole().name());
    }
}
