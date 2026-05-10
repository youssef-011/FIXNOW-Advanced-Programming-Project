package com.fix.fixnow.service;

import com.fix.fixnow.dto.MessageResponseDTO;
import com.fix.fixnow.exception.BadRequestException;
import com.fix.fixnow.exception.ResourceNotFoundException;
import com.fix.fixnow.model.Message;
import com.fix.fixnow.model.ServiceRequest;
import com.fix.fixnow.model.Technician;
import com.fix.fixnow.model.User;
import com.fix.fixnow.repository.MessageRepo;
import com.fix.fixnow.repository.ServiceRequestRepo;
import com.fix.fixnow.repository.UserRepo;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChatService {

    private final MessageRepo messageRepo;
    private final UserRepo userRepo;
    private final ServiceRequestRepo serviceRequestRepo;

    public ChatService(MessageRepo messageRepo, UserRepo userRepo, ServiceRequestRepo serviceRequestRepo) {
        this.messageRepo = messageRepo;
        this.userRepo = userRepo;
        this.serviceRequestRepo = serviceRequestRepo;
    }

    @Transactional
    public MessageResponseDTO sendMessage(Long senderId, Long receiverId, Long requestId, String content) {
        if (senderId == null) {
            throw new AccessDeniedException("Authentication required");
        }
        if (receiverId == null || requestId == null || content == null || content.trim().isEmpty()) {
            throw new BadRequestException("Receiver, request, and content are required");
        }
        if (senderId.equals(receiverId)) {
            throw new BadRequestException("Sender and receiver must be different");
        }

        User sender = userRepo.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found"));

        User receiver = userRepo.findById(receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found"));

        ServiceRequest request = serviceRequestRepo.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        if (!isParticipant(request, senderId) || !isParticipant(request, receiverId)) {
            throw new AccessDeniedException("Chat is only available to request participants");
        }

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setRequest(request);
        message.setContent(content.trim());
        message.setTimestamp(System.currentTimeMillis());

        return toDto(messageRepo.save(message));
    }

    @Transactional(readOnly = true)
    public List<MessageResponseDTO> getMessages(Long requestId, Long userId) {
        ServiceRequest request = serviceRequestRepo.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found"));

        if (!isParticipant(request, userId)) {
            throw new AccessDeniedException("Chat is only available to request participants");
        }

        return messageRepo.findByRequest_IdOrderByTimestampAsc(requestId)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private boolean isParticipant(ServiceRequest request, Long userId) {
        if (request == null || userId == null) {
            return false;
        }

        User customer = request.getUser();
        if (customer != null && userId.equals(customer.getId())) {
            return true;
        }

        Technician technician = request.getTechnician();
        return technician != null &&
                technician.getUser() != null &&
                userId.equals(technician.getUser().getId());
    }

    private MessageResponseDTO toDto(Message message) {
        return new MessageResponseDTO(
                message.getId(),
                message.getRequest() != null ? message.getRequest().getId() : null,
                message.getSender() != null ? message.getSender().getId() : null,
                message.getSender() != null ? message.getSender().getName() : null,
                message.getReceiver() != null ? message.getReceiver().getId() : null,
                message.getReceiver() != null ? message.getReceiver().getName() : null,
                message.getContent(),
                message.getTimestamp()
        );
    }
}
