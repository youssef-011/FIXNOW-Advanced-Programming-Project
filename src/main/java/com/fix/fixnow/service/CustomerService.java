package com.fix.fixnow.service;

import com.fix.fixnow.model.Review;
import com.fix.fixnow.model.ServiceRequest;
import com.fix.fixnow.repository.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CustomerService {

    private final ServiceRequestRepo serviceRequestRepo;
    private final ReviewRepo reviewRepo;
    private final TechnicianRepo technicianRepo;
    private final UserRepo userRepo;

    public CustomerService(
            ServiceRequestRepo serviceRequestRepo,
            ReviewRepo reviewRepo,
            TechnicianRepo technicianRepo,
            UserRepo userRepo
    ) {
        this.serviceRequestRepo = serviceRequestRepo;
        this.reviewRepo = reviewRepo;
        this.technicianRepo = technicianRepo;
        this.userRepo = userRepo;
    }

    public ServiceRequest createRequest(ServiceRequest request) {
        request.setStatus("Panding");
        return serviceRequestRepo.save(request);
    }


    public List<ServiceRequest> getMyRequests(Long customerId) {
        return serviceRequestRepo.findByUser_Id(customerId);
    }
}
