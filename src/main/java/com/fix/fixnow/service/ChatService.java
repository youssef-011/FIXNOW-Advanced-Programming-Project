package com.fix.fixnow.service;

import com.fix.fixnow.model.Message;
import com.fix.fixnow.model.ServiceRequest;
import com.fix.fixnow.model.User;
import com.fix.fixnow.repository.MessageRepo;
import com.fix.fixnow.repository.ServiceRequestRepo;
import com.fix.fixnow.repository.UserRepo;
import org.springframework.stereotype.Service;

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

    public Message sendMessage(Long senderId, Long receiverId, Long requestId, String content) {

        User sender = userRepo.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found"));

        User receiver = userRepo.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        ServiceRequest request = serviceRequestRepo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setRequest(request);
        message.setContent(content);
        message.setTimestamp(System.currentTimeMillis());

        return messageRepo.save(message);
    }

    public List<Message> getMessages(Long requestId) {
        return messageRepo.findByRequestId(requestId);
    }
}