package com.fix.fixnow.controller;

import com.fix.fixnow.repository.ServiceRequestRepo;
import com.fix.fixnow.repository.TechnicianRepo;
import com.fix.fixnow.repository.UserRepo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminViewController {

    private final ServiceRequestRepo serviceRequestRepo;
    private final TechnicianRepo technicianRepo;
    private final UserRepo userRepo;

    public AdminViewController(ServiceRequestRepo serviceRequestRepo,
                               TechnicianRepo technicianRepo,
                               UserRepo userRepo) {
        this.serviceRequestRepo = serviceRequestRepo;
        this.technicianRepo = technicianRepo;
        this.userRepo = userRepo;
    }

    @GetMapping("/adminDashboard")
    public String adminDashboard(Model model) {

        model.addAttribute("allRequests", serviceRequestRepo.findAll());
        model.addAttribute("pendingRequests", serviceRequestRepo.findAll());
        model.addAttribute("availableTechnicians", technicianRepo.findAll());

        model.addAttribute("totalUsers", userRepo.count());
        model.addAttribute("totalTechnicians", technicianRepo.count());
        model.addAttribute("pendingRequestsCount", serviceRequestRepo.count());

        return "admin-dashboard";
    }
}