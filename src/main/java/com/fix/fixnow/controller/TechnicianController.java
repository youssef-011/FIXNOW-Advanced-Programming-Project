package com.fix.fixnow.controller;

import com.fix.fixnow.dto.ServiceRequestDTO;
import com.fix.fixnow.exception.ResourceNotFoundException;
import com.fix.fixnow.model.ServiceRequest;
import com.fix.fixnow.model.Technician;
import com.fix.fixnow.service.TechnicianService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/technicians")
public class TechnicianController {

    private final TechnicianService technicianService;

    public TechnicianController(TechnicianService technicianService) {
        this.technicianService = technicianService;
    }

    @GetMapping("/requests/available")
    public List<ServiceRequestDTO> getAvailableRequests() {
        return technicianService.getAvailableRequests().stream()
                .map(this::toServiceRequestDTO)
                .toList();
    }

    @PostMapping("/{technicianId}/requests/{requestId}/accept")
    public ServiceRequestDTO acceptRequest(@PathVariable Long technicianId, @PathVariable Long requestId) {
        return toServiceRequestDTO(technicianService.acceptRequest(requestId, technicianId));
    }

    @PostMapping("/requests/{requestId}/complete")
    public ServiceRequestDTO completeRequest(@PathVariable Long requestId) {
        return toServiceRequestDTO(technicianService.completeRequest(requestId));
    }

    @GetMapping("/{technicianId}")
    public Technician getProfile(@PathVariable Long technicianId) {
        return technicianService.getTechnicianProfile(technicianId)
                .orElseThrow(() -> new ResourceNotFoundException("Technician not found"));
    }

    private ServiceRequestDTO toServiceRequestDTO(ServiceRequest request) {
        ServiceRequestDTO dto = new ServiceRequestDTO();
        dto.setId(request.getId());
        dto.setDescription(request.getDescription());
        dto.setStatus(request.getStatus());
        dto.setUserId(request.getUser() != null ? request.getUser().getId() : null);
        dto.setTechnicianId(request.getTechnician() != null ? request.getTechnician().getId() : null);
        return dto;
    }
}
