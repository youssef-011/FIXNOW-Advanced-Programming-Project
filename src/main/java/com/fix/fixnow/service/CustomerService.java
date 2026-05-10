package com.fix.fixnow.service;

import com.fix.fixnow.exception.BadRequestException;
import com.fix.fixnow.model.Review;
import com.fix.fixnow.model.ServiceRequest;
import com.fix.fixnow.model.Technician;
import com.fix.fixnow.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerService {

    private final ServiceRequestRepo serviceRequestRepo;
    private final ReviewRepo reviewRepo;
    private final TechnicianRepo technicianRepo;
    private final TechnicianMatchingService technicianMatchingService;

    public CustomerService(
            ServiceRequestRepo serviceRequestRepo,
            ReviewRepo reviewRepo,
            TechnicianRepo technicianRepo,
            TechnicianMatchingService technicianMatchingService
    ) {
        this.serviceRequestRepo = serviceRequestRepo;
        this.reviewRepo = reviewRepo;
        this.technicianRepo = technicianRepo;
        this.technicianMatchingService = technicianMatchingService;
    }

    @Transactional
    public ServiceRequest createRequest(ServiceRequest request) {
        request.setStatus(ServiceRequest.PENDING);
        technicianMatchingService.findBestAvailableMatch(request.getCategory()).ifPresent(technician -> {
            request.setTechnician(technician);
            request.setStatus(ServiceRequest.ASSIGNED);
            technician.setAvailable(false);
        });
        return serviceRequestRepo.save(request);
    }

    public List<ServiceRequest> getMyRequests(Long customerId) {
        return serviceRequestRepo.findByUser_Id(customerId);
    }

    @Transactional
    public Review addReview(Review review) {
        if (review.getRequest() == null || review.getRequest().getId() == null) {
            throw new BadRequestException("Review must be linked to a request");
        }

        ServiceRequest request = serviceRequestRepo.findById(review.getRequest().getId())
                .orElseThrow(() -> new BadRequestException("Request not found"));

        if (request.getUser() == null || review.getUser() == null ||
                !request.getUser().getId().equals(review.getUser().getId())) {
            throw new BadRequestException("Review does not belong to this customer");
        }

        if (request.getTechnician() == null) {
            throw new BadRequestException("Request does not have an assigned technician");
        }

        if (!ServiceRequest.COMPLETED.equals(request.getStatus())) {
            throw new BadRequestException("Request must be completed before adding a review");
        }

        if (reviewRepo.existsByRequest_Id(request.getId())) {
            throw new BadRequestException("You already reviewed this request");
        }

        Technician fullTech = technicianRepo.findById(request.getTechnician().getId())
                .orElseThrow(() -> new RuntimeException("Technician not found"));

        review.setRequest(request);
        review.setTechnician(fullTech);
        Review savedReview = reviewRepo.save(review);

        List<Review> reviews = reviewRepo.findByTechnician_Id(fullTech.getId());
        double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(savedReview.getRating());
        fullTech.setRating(avg);
        technicianRepo.save(fullTech);

        return savedReview;
    }
}
