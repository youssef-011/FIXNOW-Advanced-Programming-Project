package com.fix.fixnow.controller;

import com.fix.fixnow.dto.LoginDTO;
import com.fix.fixnow.model.Role;
import com.fix.fixnow.model.User;
import com.fix.fixnow.security.SessionAuthConstants;
import com.fix.fixnow.service.AuthService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/")
    public String home(HttpSession session) {
        return redirectBySessionRole(session, "redirect:/login");
    }

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        return redirectBySessionRole(session, "login");
    }

    @GetMapping("/register")
    public String registerPage(HttpSession session) {
        return redirectBySessionRole(session, "register");
    }

    @GetMapping("/access-denied")
    public String accessDenied(HttpSession session) {
        return redirectBySessionRole(session, "redirect:/login?error=access_denied", "?error=access_denied");
    }

    @PostMapping("/register")
    public String register(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) String description,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        Role requestedRole = publicRegistrationRole(role);
        if (isBlank(name) || isBlank(email) || isBlank(phone) || isBlank(password) || requestedRole == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please complete all registration fields correctly.");
            return "redirect:/register?error";
        }
        if (Role.ADMIN.equals(requestedRole)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Admin accounts cannot be created from public registration.");
            return "redirect:/register?error=role_not_allowed";
        }
        if (Role.TECHNICIAN.equals(requestedRole) && (isBlank(skill) || isBlank(description))) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please choose your profession and add a short technician description.");
            return "redirect:/register?error=technician_profile_required&role=TECHNICIAN";
        }

        User user = new User();
        user.setName(name.trim());
        user.setEmail(email.trim());
        user.setPassword(password);
        user.setPhone(phone.trim());
        user.setRole(requestedRole);

        try {
            User savedUser = authService.register(user, skill, description);
            session.invalidate();
            redirectAttributes.addFlashAttribute("successMessage", "Account created successfully");
            return "redirect:/login?success=registered";
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Registration failed. Please check your details or use another email.");
            return "redirect:/register?error";
        }
    }

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute LoginDTO loginDTO, BindingResult bindingResult, HttpSession session, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid email or password.");
            return "redirect:/login?error";
        }

        return authService.login(loginDTO.getEmail(), loginDTO.getPassword())
                .map(user -> {
                    storeUserInSession(session, user);
                    return redirectForRole(user);
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Invalid email or password.");
                    return "redirect:/login?error";
                });
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        SecurityContextHolder.clearContext();
        session.invalidate();
        return "redirect:/login?logout";
    }

    private String redirectForRole(User user) {
        return switch (user.getRole()) {
            case CUSTOMER -> "redirect:/customer/dashboard";
            case TECHNICIAN -> "redirect:/technician/dashboard";
            case ADMIN -> "redirect:/admin/dashboard";
        };
    }

    private String redirectBySessionRole(HttpSession session, String guestDestination) {
        return redirectBySessionRole(session, guestDestination, "");
    }

    private String redirectBySessionRole(HttpSession session, String guestDestination, String suffix) {
        Object role = session.getAttribute(SessionAuthConstants.AUTH_ROLE);
        if (!(role instanceof String roleName)) {
            return guestDestination;
        }

        return switch (roleName) {
            case "ADMIN" -> "redirect:/admin/dashboard" + suffix;
            case "TECHNICIAN" -> "redirect:/technician/dashboard" + suffix;
            case "CUSTOMER", "USER" -> "redirect:/customer/dashboard" + suffix;
            default -> {
                session.invalidate();
                yield guestDestination;
            }
        };
    }

    private Role publicRegistrationRole(String role) {
        if (role == null) {
            return null;
        }

        return switch (role.trim().toUpperCase()) {
            case "CUSTOMER", "USER" -> Role.CUSTOMER;
            case "TECHNICIAN" -> Role.TECHNICIAN;
            case "ADMIN" -> Role.ADMIN;
            default -> null;
        };
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private void storeUserInSession(HttpSession session, User user) {
        session.setAttribute(SessionAuthConstants.AUTH_USER_ID, user.getId());
        session.setAttribute(SessionAuthConstants.AUTH_NAME, user.getName());
        session.setAttribute(SessionAuthConstants.AUTH_EMAIL, user.getEmail());
        session.setAttribute(SessionAuthConstants.AUTH_ROLE, user.getRole().name());
    }
}


