package com.fix.fixnow.controller;

import com.fix.fixnow.repository.ReviewRepo;
import com.fix.fixnow.repository.ServiceRequestRepo;
import com.fix.fixnow.repository.TechnicianRepo;
import com.fix.fixnow.repository.UserRepo;
import com.fix.fixnow.security.SessionAuthConstants;
import com.fix.fixnow.service.DispatchService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final DispatchService dispatchService;
    private final UserRepo userRepo;
    private final TechnicianRepo technicianRepo;
    private final ServiceRequestRepo serviceRequestRepo;
    private final ReviewRepo reviewRepo;

    public AdminController(
            DispatchService dispatchService,
            UserRepo userRepo,
            TechnicianRepo technicianRepo,
            ServiceRequestRepo serviceRequestRepo,
            ReviewRepo reviewRepo
    ) {
        this.dispatchService = dispatchService;
        this.userRepo = userRepo;
        this.technicianRepo = technicianRepo;
        this.serviceRequestRepo = serviceRequestRepo;
        this.reviewRepo = reviewRepo;
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
        model.addAttribute("usersCount", userRepo.count());
        model.addAttribute("techniciansCount", technicianRepo.count());
        model.addAttribute("requestsCount", serviceRequestRepo.count());
        model.addAttribute("reviewsCount", reviewRepo.count());
        var pendingRequests = dispatchService.getPendingRequests();
        Map<Long, ?> matchingTechniciansByRequestId = pendingRequests.stream()
                .collect(Collectors.toMap(
                        request -> request.getId(),
                        dispatchService::getAvailableTechniciansForRequest
                ));
        model.addAttribute("pendingRequests", pendingRequests);
        model.addAttribute("availableTechnicians", dispatchService.getAvailableTechnicians());
        model.addAttribute("matchingTechniciansByRequestId", matchingTechniciansByRequestId);
        model.addAttribute("systemStatus", "Ready for dispatch");
        return "adminDashboard";
    }

    @PostMapping("/request/{requestId}/assign/{technicianId}")
    public String assignTechnician(@PathVariable Long requestId, @PathVariable Long technicianId, RedirectAttributes redirectAttributes) {
        try {
            dispatchService.assignTechnician(requestId, technicianId);
            redirectAttributes.addFlashAttribute("successMessage", "Technician assigned successfully.");
            return "redirect:/admin/dashboard?success=technician_assigned";
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not assign technician: " + ex.getMessage());
            return "redirect:/admin/dashboard?error=assign_failed";
        }
    }
}
