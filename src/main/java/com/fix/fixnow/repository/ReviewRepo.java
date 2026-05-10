package com.fix.fixnow.repository;

import com.fix.fixnow.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepo extends JpaRepository<Review, Long> {
    boolean existsByUser_IdAndTechnician_Id(Long userId, Long technicianId);
    boolean existsByRequest_Id(Long requestId);
    List<Review> findByTechnician_Id(Long technicianId);
}
