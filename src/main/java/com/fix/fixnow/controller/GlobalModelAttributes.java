package com.fix.fixnow.controller;

import com.fix.fixnow.security.SessionAuthConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute
    public void addSessionAttributes(Model model, HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }

        model.addAttribute("currentUserEmail", session.getAttribute(SessionAuthConstants.AUTH_EMAIL));
        model.addAttribute("currentUserName", session.getAttribute(SessionAuthConstants.AUTH_NAME));
        model.addAttribute("currentUserRole", session.getAttribute(SessionAuthConstants.AUTH_ROLE));
    }
}
