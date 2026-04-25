package com.fix.fixnow.controller;

import com.fix.fixnow.dto.ReviewDTO;
import com.fix.fixnow.dto.ServiceRequestDTO;
import com.fix.fixnow.exception.BadRequestException;
import com.fix.fixnow.exception.ResourceNotFoundException;
import com.fix.fixnow.model.Review;
import com.fix.fixnow.model.ServiceRequest;
import com.fix.fixnow.model.Technician;
import com.fix.fixnow.model.User;
import com.fix.fixnow.repository.TechnicianRepo;
import com.fix.fixnow.repository.UserRepo;
import com.fix.fixnow.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {

    private final CustomerService customerService;
    private final UserRepo userRepo;
    private final TechnicianRepo technicianRepo;

    public CustomerController(CustomerService customerService, UserRepo userRepo, TechnicianRepo technicianRepo) {
        this.customerService = customerService;
        this.userRepo = userRepo;
        this.technicianRepo = technicianRepo;
    }

    @PostMapping("/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceRequestDTO createRequest(@Valid @RequestBody ServiceRequestDTO dto) {
        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        ServiceRequest request = new ServiceRequest();
        request.setDescription(dto.getDescription());
        request.setUser(user);
        return toServiceRequestDTO(customerService.createRequest(request));
    }

    @GetMapping("/{customerId}/requests")
    public List<ServiceRequestDTO> getMyRequests(@PathVariable Long customerId) {
        return customerService.getMyRequests(customerId).stream()
                .map(this::toServiceRequestDTO)
                .toList();
    }

    @PostMapping("/reviews")
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewDTO addReview(@Valid @RequestBody ReviewDTO dto) {
        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Technician technician = technicianRepo.findById(dto.getTechnicianId())
                .orElseThrow(() -> new ResourceNotFoundException("Technician not found"));

        Review review = new Review();
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setUser(user);
        review.setTechnician(technician);

        return toReviewDTO(customerService.addReview(review));
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

    private ReviewDTO toReviewDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setUserId(review.getUser() != null ? review.getUser().getId() : null);
        dto.setTechnicianId(review.getTechnician() != null ? review.getTechnician().getId() : null);
        return dto;
    }
}
