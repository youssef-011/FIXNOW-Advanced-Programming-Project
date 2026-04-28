package com.fix.fixnow.service;

import com.fix.fixnow.exception.BadRequestException;
import com.fix.fixnow.model.Review;
import com.fix.fixnow.model.ServiceRequest;
import com.fix.fixnow.model.Technician;
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
        request.setStatus("Pending");
        return serviceRequestRepo.save(request);
    }

    public List<ServiceRequest> getMyRequests(Long customerId) {
        return serviceRequestRepo.findByUser_Id(customerId);
    }

    public Review addReview(Review review) {
        ServiceRequest request = serviceRequestRepo
                .findByUser_IdAndTechnician_Id(
                        review.getUser().getId(),
                        review.getTechnician().getId())
                .orElseThrow(() -> new BadRequestException("No request found between this user and technician"));

        if (!request.getStatus().equals("COMPLETED")) {
            throw new BadRequestException("Request must be completed before adding a review");
        }

        Technician fullTech = technicianRepo.findById(review.getTechnician().getId())
                .orElseThrow(() -> new RuntimeException("Technician not found"));

        List<Review> reviews = fullTech.getReviews();
        reviews.add(review);
        double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(review.getRating());
        fullTech.setRating(avg);
        technicianRepo.save(fullTech);

        return reviewRepo.save(review);
    }
}