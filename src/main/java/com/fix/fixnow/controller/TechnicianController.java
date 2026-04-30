package com.fix.fixnow.controller;

import com.fix.fixnow.model.ServiceRequest;
import com.fix.fixnow.model.Technician;
import com.fix.fixnow.repository.TechnicianRepo;
import com.fix.fixnow.security.SessionAuthConstants;
import com.fix.fixnow.service.TechnicianService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/technician")
public class TechnicianController {

    private final TechnicianService technicianService;
    private final TechnicianRepo technicianRepo;

    public TechnicianController(TechnicianService technicianService, TechnicianRepo technicianRepo) {
        this.technicianService = technicianService;
        this.technicianRepo = technicianRepo;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        List<ServiceRequest> availableRequests = technicianService.getAvailableRequests();
        model.addAttribute("availableRequests", availableRequests);
        model.addAttribute("availableRequestsCount", availableRequests.size());

        Object technicianName = session.getAttribute(SessionAuthConstants.AUTH_NAME);
        if (technicianName != null) {
            model.addAttribute("technicianName", technicianName);
        }

        return "technicianDashboard";
    }

    @PostMapping("/request/{id}/accept")
    public String acceptRequest(@PathVariable("id") Long requestId, HttpSession session) {
        Long technicianId = currentTechnicianId(session);
        if (technicianId == null) {
            return "redirect:/login";
        }

        technicianService.acceptRequest(requestId, technicianId);
        return "redirect:/technician/dashboard";
    }

    @PostMapping("/request/{id}/complete")
    public String completeRequest(@PathVariable("id") Long requestId, HttpSession session) {
        Long technicianId = currentTechnicianId(session);
        if (technicianId == null) {
            return "redirect:/login";
        }

        technicianService.completeRequest(requestId, technicianId);
        return "redirect:/technician/dashboard";
    }

    private Long currentTechnicianId(HttpSession session) {
        Long userId = currentUserId(session);
        if (userId == null) {
            return null;
        }

        Technician technician = technicianRepo.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Technician profile not found"));

        return technician.getId();
    }

    private Long currentUserId(HttpSession session) {
        Object userId = session.getAttribute(SessionAuthConstants.AUTH_USER_ID);
        if (userId instanceof Long id) {
            return id;
        }
        if (userId instanceof Number number) {
            return number.longValue();
        }
        return null;
    }
}
