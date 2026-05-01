package com.fix.fixnow.service;

import com.fix.fixnow.model.Technician;
import com.fix.fixnow.model.ServiceRequest;
import com.fix.fixnow.repository.TechnicianRepo;
import com.fix.fixnow.repository.ServiceRequestRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class TechnicianService {

    private final TechnicianRepo technicianRepo;
    private final ServiceRequestRepo serviceRequestRepo;

    public TechnicianService(
            TechnicianRepo technicianRepo,
            ServiceRequestRepo serviceRequestRepo
    ) {
        this.technicianRepo = technicianRepo;
        this.serviceRequestRepo = serviceRequestRepo;
    }

    public List<ServiceRequest> getAvailableRequests() {
        return serviceRequestRepo.findByStatus(ServiceRequest.PENDING);
    }

    public List<ServiceRequest> getAssignedRequests(Long technicianId) {
        return serviceRequestRepo.findByTechnician_Id(technicianId);
    }

    @Transactional
    public ServiceRequest acceptRequest(Long requestId, Long technicianId) {
        ServiceRequest request = serviceRequestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Sorry your order is currently unavailable, please wait"));

        if (request.getTechnician() != null) {
            throw new RuntimeException("This request is already taken");
        }

        Technician technician = technicianRepo.findById(technicianId)
                .orElseThrow(() -> new RuntimeException("Technicians are currently busy"));

        request.setTechnician(technician);
        technician.setAvailable(false);
        request.setStatus("ACCEPTED");
        return serviceRequestRepo.save(request);
    }

    @Transactional
    public ServiceRequest completeRequest(Long requestId, Long technicianId) {
        ServiceRequest request = serviceRequestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Sorry your order is currently unavailable, please wait"));

        if (request.getTechnician() == null ||
                !request.getTechnician().getId().equals(technicianId)) {
            throw new RuntimeException("This request is not assigned to you");
        }
        request.getTechnician().setAvailable(true);
        request.setStatus("COMPLETED");
        return serviceRequestRepo.save(request);
    }

    public Optional<Technician> getTechnicianProfile(Long technicianId) {
        return technicianRepo.findById(technicianId);
    }
}
