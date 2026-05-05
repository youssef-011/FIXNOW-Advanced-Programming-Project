package com.fix.fixnow.controller;

import com.fix.fixnow.dto.ReviewDTO;
import com.fix.fixnow.dto.ServiceRequestDTO;
import com.fix.fixnow.exception.ResourceNotFoundException;
import com.fix.fixnow.model.Review;
import com.fix.fixnow.model.ServiceRequest;
import com.fix.fixnow.model.Technician;
import com.fix.fixnow.model.User;
import com.fix.fixnow.repository.ServiceRequestRepo;
import com.fix.fixnow.repository.TechnicianRepo;
import com.fix.fixnow.repository.UserRepo;
import com.fix.fixnow.security.SessionAuthConstants;
import com.fix.fixnow.service.CustomerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class CustomerController {

    private final CustomerService customerService;
    private final ServiceRequestRepo serviceRequestRepo;
    private final UserRepo userRepo;
    private final TechnicianRepo technicianRepo;

    public CustomerController(CustomerService customerService, ServiceRequestRepo serviceRequestRepo, UserRepo userRepo, TechnicianRepo technicianRepo) {
        this.customerService = customerService;
        this.serviceRequestRepo = serviceRequestRepo;
        this.userRepo = userRepo;
        this.technicianRepo = technicianRepo;
    }

    @GetMapping("/customer/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Long userId = currentUserId(session);
        if (userId == null) {
            return "redirect:/login";
        }

        List<ServiceRequest> requests = customerService.getMyRequests(userId);
        model.addAttribute("requests", requests);
        model.addAttribute("activeRequestsCount", requests.stream().filter(request -> !ServiceRequest.COMPLETED.equals(request.getStatus())).count());
        model.addAttribute("completedRequestsCount", requests.stream().filter(request -> ServiceRequest.COMPLETED.equals(request.getStatus())).count());
        model.addAttribute("pendingReviewsCount", requests.stream().filter(request -> ServiceRequest.COMPLETED.equals(request.getStatus())).count());
        model.addAttribute("nearbyTechniciansCount", technicianRepo.findByAvailable(true).size());

        requests.stream().findFirst().ifPresent(request -> {
            model.addAttribute("activeRequestTitle", "Request #" + request.getId());
            model.addAttribute("activeRequestStatus", request.getStatus());
        });

        Object customerName = session.getAttribute(SessionAuthConstants.AUTH_NAME);
        if (customerName != null) {
            model.addAttribute("customerName", customerName);
        }

        return "customerDashboard";
    }

    @GetMapping("/customer/request/new")
    public String createRequestPage() {
        return "createRequest";
    }

    @GetMapping("/customer/request/{id}")
    public String requestDetails(@PathVariable Long id, HttpSession session, Model model) {
        Long userId = currentUserId(session);
        if (userId == null) {
            return "redirect:/login";
        }

        Optional<ServiceRequest> optionalRequest = serviceRequestRepo.findById(id);
        if (optionalRequest.isEmpty()) {
            return "redirect:/customer/dashboard?error=request_not_found";
        }

        ServiceRequest request = optionalRequest.get();
        if (request.getUser() == null || !userId.equals(request.getUser().getId())) {
            return "redirect:/access-denied";
        }

        model.addAttribute("request", request);
        model.addAttribute("requestId", request.getId());
        model.addAttribute("requestTitle", "Request #" + request.getId());
        model.addAttribute("requestDescription", request.getDescription());
        model.addAttribute("requestStatus", request.getStatus());
        model.addAttribute("requestCategory", request.getCategory());
        model.addAttribute("requestUrgency", request.getUrgency());
        model.addAttribute("requestLocation", request.getLocation());
        if (request.getTechnician() != null) {
            model.addAttribute("technicianName", request.getTechnician().getName());
            model.addAttribute("technicianSkill", request.getTechnician().getSkill());
            model.addAttribute("technicianRating", request.getTechnician().getRating());
        }
        return "requestDetails";
    }

    @GetMapping("/customer/review/new")
    public String addReviewPage() {
        return "addReview";
    }

    @PostMapping("/customer/request/new")
    public String createRequest(@ModelAttribute ServiceRequestDTO dto, HttpSession session, RedirectAttributes redirectAttributes) {
        Long userId = currentUserId(session);
        if (userId == null) {
            return "redirect:/login";
        }
        if (!hasText(dto.getDescription()) || !hasText(dto.getCategory()) || !hasText(dto.getLocation()) || !hasText(dto.getUrgency())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please complete all request fields.");
            return "redirect:/customer/request/new?error=invalid_request";
        }

        Optional<User> optionalUser = userRepo.findById(userId);
        if (optionalUser.isEmpty()) {
            session.invalidate();
            return "redirect:/login?error=session_expired";
        }

        ServiceRequest request = new ServiceRequest();
        request.setDescription(dto.getDescription().trim());
        request.setCategory(dto.getCategory().trim());
        request.setLocation(dto.getLocation().trim());
        request.setUrgency(dto.getUrgency().trim());
        request.setUser(optionalUser.get());

        try {
            customerService.createRequest(request);
            redirectAttributes.addFlashAttribute("successMessage", "Service request created successfully.");
            return "redirect:/customer/dashboard?success=request_created";
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not create request. Please try again.");
            return "redirect:/customer/request/new?error=request_failed";
        }
    }

    @PostMapping("/customer/review/new")
    public String addReview(@ModelAttribute ReviewDTO dto, HttpSession session, RedirectAttributes redirectAttributes) {
        Long userId = currentUserId(session);
        if (userId == null) {
            return "redirect:/login";
        }
        if (dto.getRequestId() == null || dto.getRating() < 1 || dto.getRating() > 5 || !hasText(dto.getComment())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please choose a completed request and provide a valid review.");
            return "redirect:/customer/dashboard?error=invalid_review";
        }

        Optional<User> optionalUser = userRepo.findById(userId);
        if (optionalUser.isEmpty()) {
            session.invalidate();
            return "redirect:/login?error=session_expired";
        }

        Optional<ServiceRequest> optionalRequest = serviceRequestRepo.findById(dto.getRequestId());
        if (optionalRequest.isEmpty()) {
            return "redirect:/customer/dashboard?error=request_not_found";
        }

        ServiceRequest serviceRequest = optionalRequest.get();
        if (serviceRequest.getUser() == null || !userId.equals(serviceRequest.getUser().getId())) {
            return "redirect:/access-denied";
        }
        if (serviceRequest.getTechnician() == null) {
            return "redirect:/customer/dashboard?error=review_not_available";
        }

        Review review = new Review();
        review.setRating(dto.getRating());
        review.setComment(dto.getComment().trim());
        review.setUser(optionalUser.get());
        review.setTechnician(serviceRequest.getTechnician());
        review.setRequest(serviceRequest);

        try {
            customerService.addReview(review);
            redirectAttributes.addFlashAttribute("successMessage", "Review submitted successfully.");
            return "redirect:/customer/dashboard?success=review_submitted";
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not submit review. Please try again.");
            return "redirect:/customer/dashboard?error=review_failed";
        }
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

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
