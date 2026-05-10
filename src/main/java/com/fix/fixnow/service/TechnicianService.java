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
    private final TechnicianMatchingService technicianMatchingService;

    public TechnicianService(
            TechnicianRepo technicianRepo,
            ServiceRequestRepo serviceRequestRepo,
            TechnicianMatchingService technicianMatchingService
    ) {
        this.technicianRepo = technicianRepo;
        this.serviceRequestRepo = serviceRequestRepo;
        this.technicianMatchingService = technicianMatchingService;
    }

    public List<ServiceRequest> getAvailableRequests() {
        return serviceRequestRepo.findByStatusAndTechnicianIsNull(ServiceRequest.PENDING);
    }

    public List<ServiceRequest> getAvailableRequestsForTechnician(Long technicianId) {
        Technician technician = technicianRepo.findById(technicianId)
                .orElseThrow(() -> new RuntimeException("Technician profile not found"));

        if (!technician.isAvailable()) {
            return List.of();
        }

        return serviceRequestRepo.findByStatusAndTechnicianIsNull(ServiceRequest.PENDING).stream()
                .filter(request -> technicianMatchingService.matchesCategory(technician, request.getCategory()))
                .toList();
    }

    public List<ServiceRequest> getAssignedRequests(Long technicianId) {
        return serviceRequestRepo.findByTechnician_Id(technicianId);
    }

    @Transactional
    public ServiceRequest acceptRequest(Long requestId, Long technicianId) {
        ServiceRequest request = serviceRequestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Sorry your order is currently unavailable, please wait"));

        Technician technician = technicianRepo.findById(technicianId)
                .orElseThrow(() -> new RuntimeException("Technicians are currently busy"));

        if (ServiceRequest.ASSIGNED.equals(request.getStatus())) {
            if (request.getTechnician() == null ||
                    !request.getTechnician().getId().equals(technicianId)) {
                throw new RuntimeException("This request is not assigned to you");
            }
            request.setStatus(ServiceRequest.ACCEPTED);
            return serviceRequestRepo.save(request);
        }

        if (!ServiceRequest.PENDING.equals(request.getStatus())) {
            throw new RuntimeException("Only PENDING or assigned requests can be accepted");
        }

        if (request.getTechnician() != null) {
            throw new RuntimeException("This request is already taken");
        }

        if (!technician.isAvailable()) {
            throw new RuntimeException("Technician is not available");
        }

        if (!technicianMatchingService.matchesCategory(technician, request.getCategory())) {
            throw new RuntimeException("Technician skill does not match this request category");
        }

        request.setTechnician(technician);
        technician.setAvailable(false);
        request.setStatus(ServiceRequest.ACCEPTED);
        return serviceRequestRepo.save(request);
    }

    @Transactional
    public ServiceRequest completeRequest(Long requestId, Long technicianId) {
        ServiceRequest request = serviceRequestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Sorry your order is currently unavailable, please wait"));

        if (!request.getStatus().equals(ServiceRequest.ACCEPTED)) {
            throw new RuntimeException("Only ACCEPTED requests can be completed");
        }

        if (request.getTechnician() == null ||
                !request.getTechnician().getId().equals(technicianId)) {
            throw new RuntimeException("This request is not assigned to you");
        }

        request.getTechnician().setAvailable(true);
        request.setStatus(ServiceRequest.COMPLETED);
        return serviceRequestRepo.save(request);
    }

    public Optional<Technician> getTechnicianProfile(Long technicianId) {
        return technicianRepo.findById(technicianId);
    }

    @Transactional
    public Technician updateAvailability(Long technicianId, boolean available) {
        Technician technician = technicianRepo.findById(technicianId)
                .orElseThrow(() -> new RuntimeException("Technician profile not found"));

        if (available && hasActiveAssignedJobs(technicianId)) {
            throw new RuntimeException("Complete active jobs before becoming available");
        }

        technician.setAvailable(available);
        return technicianRepo.save(technician);
    }

    public boolean hasActiveAssignedJobs(Long technicianId) {
        return serviceRequestRepo.findByTechnician_Id(technicianId).stream()
                .anyMatch(request -> ServiceRequest.ASSIGNED.equals(request.getStatus()) ||
                        ServiceRequest.ACCEPTED.equals(request.getStatus()));
    }
}
