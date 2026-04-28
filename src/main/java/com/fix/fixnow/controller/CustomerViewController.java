package com.fix.fixnow.controller;

import com.fix.fixnow.model.ServiceRequest;
import com.fix.fixnow.repository.ServiceRequestRepo;
import com.fix.fixnow.repository.UserRepo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping
public class CustomerViewController {

    private final ServiceRequestRepo serviceRequestRepo;
    private final UserRepo userRepo;

    public CustomerViewController(ServiceRequestRepo serviceRequestRepo, UserRepo userRepo) {
        this.serviceRequestRepo = serviceRequestRepo;
        this.userRepo = userRepo;
    }

    @GetMapping("/customerDashboard")
    public String dashboard(Model model) {
        List<ServiceRequest> requests = serviceRequestRepo.findAll();

        model.addAttribute("requests", requests);
        model.addAttribute("activeRequestsCount", requests.size());
        model.addAttribute("completedRequestsCount",
                requests.stream().filter(r -> "COMPLETED".equals(r.getStatus())).count());

        return "customer-dashboard";
    }

    @GetMapping("/createRequest")
    public String createRequestPage(Model model) {
        model.addAttribute("pageTitle", "Create Request");
        return "create-request";
    }

    @GetMapping("/requestDetails")
    public String requestDetails(@RequestParam Long id, Model model) {
        ServiceRequest request = serviceRequestRepo.findById(id)
                .orElse(null);

        model.addAttribute("request", request);
        return "request-details";
    }

    @GetMapping("/addReview")
    public String addReview(@RequestParam Long requestId, Model model) {
        ServiceRequest request = serviceRequestRepo.findById(requestId)
                .orElse(null);

        model.addAttribute("request", request);
        return "add-review";
    }
}