package com.fix.fixnow.controller;

import com.fix.fixnow.security.SessionAuthConstants;
import com.fix.fixnow.service.DispatchService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final DispatchService dispatchService;

    public AdminController(DispatchService dispatchService) {
        this.dispatchService = dispatchService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Object adminName = session.getAttribute(SessionAuthConstants.AUTH_NAME);
        if (adminName != null) {
            model.addAttribute("adminName", adminName);
        }
        return "adminDashboard";
    }

    @PostMapping("/request/{requestId}/assign/{technicianId}")
    public String assignTechnician(@PathVariable Long requestId, @PathVariable Long technicianId) {
        dispatchService.assignTechnician(requestId, technicianId);
        return "redirect:/admin/dashboard";
    }
}
