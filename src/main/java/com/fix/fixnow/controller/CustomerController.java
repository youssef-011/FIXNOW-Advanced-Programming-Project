package com.fix.fixnow.controller;

import com.fix.fixnow.dto.ReviewDTO;
import com.fix.fixnow.dto.ServiceRequestDTO;
import com.fix.fixnow.exception.ResourceNotFoundException;
import com.fix.fixnow.model.Review;
import com.fix.fixnow.model.ServiceRequest;
import com.fix.fixnow.model.Technician;
import com.fix.fixnow.model.User;
import com.fix.fixnow.repository.TechnicianRepo;
import com.fix.fixnow.repository.UserRepo;
import com.fix.fixnow.security.SessionAuthConstants;
import com.fix.fixnow.service.CustomerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class CustomerController {

    private final CustomerService customerService;
    private final UserRepo userRepo;
    private final TechnicianRepo technicianRepo;

    public CustomerController(CustomerService customerService, UserRepo userRepo, TechnicianRepo technicianRepo) {
        this.customerService = customerService;
        this.userRepo = userRepo;
        this.technicianRepo = technicianRepo;
    }

    @GetMapping("/customer/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Long userId = currentUserId(session);
        if (userId != null) {
            model.addAttribute("requests", customerService.getMyRequests(userId));
            Object customerName = session.getAttribute(SessionAuthConstants.AUTH_NAME);
            if (customerName != null) {
                model.addAttribute("customerName", customerName);
            }
        }
        return "customerDashboard";
    }

    @GetMapping("/customer/request/new")
    public String createRequestPage() {
        return "createRequest";
    }

    @GetMapping("/customer/request/{id}")
    public String requestDetails(@PathVariable Long id, Model model) {
        model.addAttribute("requestId", id);
        return "requestDetails";
    }

    @GetMapping("/customer/review/new")
    public String addReviewPage() {
        return "addReview";
    }

    @PostMapping("/customer/request/new")
    public String createRequest(@ModelAttribute ServiceRequestDTO dto, HttpSession session) {
        Long userId = currentUserId(session);
        if (userId == null) {
            return "redirect:/login";
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ServiceRequest request = new ServiceRequest();
        request.setDescription(dto.getDescription());
        request.setCategory(dto.getCategory());
        request.setLocation(dto.getLocation());
        request.setUrgency(dto.getUrgency());
        request.setUser(user);
        customerService.createRequest(request);
        return "redirect:/customer/dashboard";
    }

    @PostMapping("/customer/review/new")
    public String addReview(@ModelAttribute ReviewDTO dto, HttpSession session) {
        Long userId = currentUserId(session);
        if (userId == null) {
            return "redirect:/login";
        }

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (dto.getTechnicianId() == null) {
            return "redirect:/customer/dashboard";
        }

        Technician technician = technicianRepo.findById(dto.getTechnicianId())
                .orElseThrow(() -> new ResourceNotFoundException("Technician not found"));

        Review review = new Review();
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setUser(user);
        review.setTechnician(technician);
        if (dto.getRequestId() != null) {
            ServiceRequest request = new ServiceRequest();
            request.setId(dto.getRequestId());
            review.setRequest(request);
        }

        customerService.addReview(review);
        return "redirect:/customer/dashboard";
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
