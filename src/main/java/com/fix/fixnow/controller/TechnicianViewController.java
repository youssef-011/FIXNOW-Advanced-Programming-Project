package com.fix.fixnow.controller;

import com.fix.fixnow.repository.ServiceRequestRepo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TechnicianViewController {

    private final ServiceRequestRepo serviceRequestRepo;

    public TechnicianViewController(ServiceRequestRepo serviceRequestRepo) {
        this.serviceRequestRepo = serviceRequestRepo;
    }

    @GetMapping("/technicianDashboard")
    public String technicianDashboard(Model model) {

        model.addAttribute("assignedJobs", serviceRequestRepo.findAll());
        model.addAttribute("availableRequests", serviceRequestRepo.findAll());

        return "technician-dashboard";
    }
}