package com.fix.fixnow.controller;

import com.fix.fixnow.dto.ServiceRequestDTO;
import com.fix.fixnow.model.ServiceRequest;
import com.fix.fixnow.model.Technician;
import com.fix.fixnow.service.DispatchService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final DispatchService dispatchService;

    public AdminController(DispatchService dispatchService) {
        this.dispatchService = dispatchService;
    }

    @GetMapping("/requests")
    public List<ServiceRequestDTO> getAllRequests() {
        return dispatchService.getAllRequests().stream()
                .map(this::toServiceRequestDTO)
                .toList();
    }

    @GetMapping("/requests/pending")
    public List<ServiceRequestDTO> getPendingRequests() {
        return dispatchService.getPendingRequests().stream()
                .map(this::toServiceRequestDTO)
                .toList();
    }

    @GetMapping("/technicians/available")
    public List<Technician> getAvailableTechnicians() {
        return dispatchService.getAvailableTechnicians();
    }

    @PostMapping("/requests/{requestId}/assign/{technicianId}")
    public ServiceRequestDTO assignTechnician(@PathVariable Long requestId, @PathVariable Long technicianId) {
        return toServiceRequestDTO(dispatchService.assignTechnician(requestId, technicianId));
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
