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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

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
        Long userId = currentUserId(session);
        if (userId == null) {
            return "redirect:/login";
        }

        List<ServiceRequest> availableRequests;
        try {
            availableRequests = technicianService.getAvailableRequests();
        } catch (RuntimeException ex) {
            availableRequests = List.of();
            model.addAttribute("errorMessage", "Could not load available requests.");
        }
        model.addAttribute("availableRequests", availableRequests);
        model.addAttribute("availableRequestsCount", availableRequests.size());

        Optional<Technician> technician = currentTechnician(session);
        if (technician.isPresent()) {
            Long technicianId = technician.get().getId();
            List<ServiceRequest> assignedRequests;
            try {
                assignedRequests = technicianService.getAssignedRequests(technicianId);
            } catch (RuntimeException ex) {
                assignedRequests = List.of();
                model.addAttribute("errorMessage", "Could not load assigned requests.");
            }
            model.addAttribute("assignedRequests", assignedRequests);
            model.addAttribute("assignedJobsCount", assignedRequests.stream().filter(request -> !ServiceRequest.COMPLETED.equals(request.getStatus())).count());
            model.addAttribute("completedJobsCount", assignedRequests.stream().filter(request -> ServiceRequest.COMPLETED.equals(request.getStatus())).count());
            model.addAttribute("availabilityStatus", technician.get().isAvailable() ? "Available" : "Busy");
            if (technician.get().getReviews() != null) {
                model.addAttribute("reviewCount", technician.get().getReviews().size());
            }
        } else {
            model.addAttribute("assignedRequests", List.of());
            model.addAttribute("assignedJobsCount", 0);
            model.addAttribute("completedJobsCount", 0);
            model.addAttribute("reviewCount", 0);
            model.addAttribute("availabilityStatus", "Profile not found");
        }

        Object technicianName = session.getAttribute(SessionAuthConstants.AUTH_NAME);
        if (technicianName != null) {
            model.addAttribute("technicianName", technicianName);
        }

        return "technicianDashboard";
    }

    @PostMapping("/request/{id}/accept")
    public String acceptRequest(@PathVariable("id") Long requestId, HttpSession session, RedirectAttributes redirectAttributes) {
        Long technicianId = currentTechnicianId(session);
        if (technicianId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Technician profile not found.");
            return "redirect:/technician/dashboard?error=profile_not_found";
        }

        try {
            technicianService.acceptRequest(requestId, technicianId);
            redirectAttributes.addFlashAttribute("successMessage", "Request accepted successfully.");
            return "redirect:/technician/dashboard?success=request_accepted";
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not accept request.");
            return "redirect:/technician/dashboard?error=accept_failed";
        }
    }

    @PostMapping("/request/{id}/complete")
    public String completeRequest(@PathVariable("id") Long requestId, HttpSession session, RedirectAttributes redirectAttributes) {
        Long technicianId = currentTechnicianId(session);
        if (technicianId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Technician profile not found.");
            return "redirect:/technician/dashboard?error=profile_not_found";
        }

        try {
            technicianService.completeRequest(requestId, technicianId);
            redirectAttributes.addFlashAttribute("successMessage", "Request completed successfully.");
            return "redirect:/technician/dashboard?success=request_completed";
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not complete request.");
            return "redirect:/technician/dashboard?error=complete_failed";
        }
    }

    private Long currentTechnicianId(HttpSession session) {
        return currentTechnician(session)
                .map(Technician::getId)
                .orElse(null);
    }

    private Optional<Technician> currentTechnician(HttpSession session) {
        Long userId = currentUserId(session);
        if (userId == null) {
            return Optional.empty();
        }
        return technicianRepo.findByUser_Id(userId);
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
