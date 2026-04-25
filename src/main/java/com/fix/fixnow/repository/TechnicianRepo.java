package com.fix.fixnow.repository;

import com.fix.fixnow.model.Technician;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TechnicianRepo extends JpaRepository<Technician, Long> {
    List<Technician> findByAvailable(boolean available);
}
