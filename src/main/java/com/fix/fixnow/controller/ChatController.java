package com.fix.fixnow.controller;

import com.fix.fixnow.dto.MessageDTO;
import com.fix.fixnow.model.Message;
import com.fix.fixnow.service.ChatService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/send")
    public Message send(@RequestBody MessageDTO dto) {
        return chatService.sendMessage(
                dto.getSenderId(),
                dto.getReceiverId(),
                dto.getRequestId(),
                dto.getContent()
        );
    }

    @GetMapping("/{requestId}")
    public List<Message> getChat(@PathVariable Long requestId) {
        return chatService.getMessages(requestId);
    }
}