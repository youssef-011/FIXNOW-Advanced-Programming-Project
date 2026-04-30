package com.fix.fixnow.repository;

import com.fix.fixnow.model.Technician;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TechnicianRepo extends JpaRepository<Technician, Long> {
    List<Technician> findByAvailable(boolean available);
    Optional<Technician> findByUser_Id(Long userId);
}
