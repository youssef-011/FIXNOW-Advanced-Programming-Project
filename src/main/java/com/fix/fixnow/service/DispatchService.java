package com.fix.fixnow.service;

import com.fix.fixnow.model.Technician;
import com.fix.fixnow.model.ServiceRequest;
import com.fix.fixnow.repository.TechnicianRepo;
import com.fix.fixnow.repository.ServiceRequestRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class DispatchService {

    private final ServiceRequestRepo serviceRequestRepo;
    private final TechnicianRepo technicianRepo;
    private final TechnicianMatchingService technicianMatchingService;

    public DispatchService(
            ServiceRequestRepo serviceRequestRepo,
            TechnicianRepo technicianRepo,
            TechnicianMatchingService technicianMatchingService
    ) {
        this.serviceRequestRepo = serviceRequestRepo;
        this.technicianRepo = technicianRepo;
        this.technicianMatchingService = technicianMatchingService;
    }

    public List<ServiceRequest> getAllRequests() {
        return serviceRequestRepo.findAll();
    }

    @Transactional
    public ServiceRequest assignTechnician(Long requestId, Long technicianId) {
        ServiceRequest request = serviceRequestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Sorry your order is currently unavailable, please wait"));

        // ← status check
        if (!request.getStatus().equals(ServiceRequest.PENDING)) {
            throw new RuntimeException("Only PENDING requests can be assigned");
        }

        if (request.getTechnician() != null) {
            throw new RuntimeException("This request is already assigned to a technician");
        }

        Technician technician = technicianRepo.findById(technicianId)
                .orElseThrow(() -> new RuntimeException("Technicians are currently busy"));

        if (!technician.isAvailable()) {
            throw new RuntimeException("Technician is not available");
        }

        if (!technicianMatchingService.matchesCategory(technician, request.getCategory())) {
            throw new RuntimeException("Technician skill does not match this request category");
        }

        request.setTechnician(technician);
        technician.setAvailable(false);
        request.setStatus(ServiceRequest.ASSIGNED);
        return serviceRequestRepo.save(request);
    }

    public List<Technician> getAvailableTechnicians() {
        return technicianRepo.findByAvailable(true);
    }

    public List<Technician> getAvailableTechniciansForRequest(ServiceRequest request) {
        return technicianMatchingService.findAvailableMatches(request.getCategory());
    }

    public List<ServiceRequest> getPendingRequests() {
        return serviceRequestRepo.findByStatusAndTechnicianIsNull(ServiceRequest.PENDING);
    }
}
