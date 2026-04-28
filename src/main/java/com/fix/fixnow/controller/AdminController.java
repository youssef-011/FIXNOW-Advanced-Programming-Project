package com.fix.fixnow.controller;

import com.fix.fixnow.service.DispatchService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final DispatchService dispatchService;

    public AdminController(DispatchService dispatchService) {
        this.dispatchService = dispatchService;
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        return "adminDashboard";
    }

    @PostMapping("/request/{requestId}/assign/{technicianId}")
    public String assignTechnician(@PathVariable Long requestId, @PathVariable Long technicianId) {
        dispatchService.assignTechnician(requestId, technicianId);
        return "redirect:/admin/dashboard";
    }
}
