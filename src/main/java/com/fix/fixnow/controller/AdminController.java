package com.fix.fixnow.controller;

import com.fix.fixnow.security.SessionAuthConstants;
import com.fix.fixnow.service.DispatchService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final DispatchService dispatchService;

    public AdminController(DispatchService dispatchService) {
        this.dispatchService = dispatchService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (session.getAttribute(SessionAuthConstants.AUTH_USER_ID) == null) {
            return "redirect:/login";
        }

        Object adminName = session.getAttribute(SessionAuthConstants.AUTH_NAME);
        if (adminName != null) {
            model.addAttribute("adminName", adminName);
        }
        return "adminDashboard";
    }

    @PostMapping("/request/{requestId}/assign/{technicianId}")
    public String assignTechnician(@PathVariable Long requestId, @PathVariable Long technicianId, RedirectAttributes redirectAttributes) {
        try {
            dispatchService.assignTechnician(requestId, technicianId);
            redirectAttributes.addFlashAttribute("successMessage", "Technician assigned successfully.");
            return "redirect:/admin/dashboard?success=technician_assigned";
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not assign technician.");
            return "redirect:/admin/dashboard?error=assign_failed";
        }
    }
}
