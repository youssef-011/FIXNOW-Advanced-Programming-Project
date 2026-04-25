package com.fix.fixnow.repository;

import com.fix.fixnow.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepo extends JpaRepository<Review, Long> {
}
