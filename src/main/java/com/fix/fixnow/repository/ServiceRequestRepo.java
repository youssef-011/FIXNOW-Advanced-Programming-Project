package com.fix.fixnow.repository;

import com.fix.fixnow.model.ServiceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ServiceRequestRepo extends JpaRepository<ServiceRequest, Long> {
    List<ServiceRequest> findByUser_Id(Long userId);
    List<ServiceRequest> findByStatus(String status);
    Optional<ServiceRequest> findByUser_IdAndTechnician_Id(Long userId, Long technicianId);
}