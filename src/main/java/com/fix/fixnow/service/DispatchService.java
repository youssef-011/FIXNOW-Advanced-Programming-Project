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

    public DispatchService(
            ServiceRequestRepo serviceRequestRepo,
            TechnicianRepo technicianRepo
    ) {
        this.serviceRequestRepo = serviceRequestRepo;
        this.technicianRepo = technicianRepo;
    }

    public List<ServiceRequest> getAllRequests() {
        return serviceRequestRepo.findAll();
    }

    @Transactional
    public ServiceRequest assignTechnician(Long requestId, Long technicianId) {
        ServiceRequest request = serviceRequestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Sorry your order is currently unavailable, please wait"));

        if (request.getTechnician() != null) {
            throw new RuntimeException("This request is already assigned to a technician");
        }

        Technician technician = technicianRepo.findById(technicianId)
                .orElseThrow(() -> new RuntimeException("Technicians are currently busy"));

        request.setTechnician(technician);
        technician.setAvailable(false);
        request.setStatus("ASSIGNED");
        return serviceRequestRepo.save(request);
    }

    public List<Technician> getAvailableTechnicians() {
        return technicianRepo.findByAvailable(true);
    }

    public List<ServiceRequest> getPendingRequests() {
        return serviceRequestRepo.findByStatus(ServiceRequest.PENDING);
    }
}