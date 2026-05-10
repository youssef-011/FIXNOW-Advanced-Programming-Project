package com.fix.fixnow.repository;

import com.fix.fixnow.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepo extends JpaRepository<Message, Long> {
    List<Message> findByRequestId(Long requestId);
    List<Message> findByRequest_IdOrderByTimestampAsc(Long requestId);
}
