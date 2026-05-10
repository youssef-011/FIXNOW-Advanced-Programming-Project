package com.fix.fixnow.controller;

import com.fix.fixnow.dto.MessageDTO;
import com.fix.fixnow.dto.MessageResponseDTO;
import com.fix.fixnow.security.SessionAuthConstants;
import com.fix.fixnow.service.ChatService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/send")
    public MessageResponseDTO send(@Valid @RequestBody MessageDTO dto, HttpSession session) {
        return chatService.sendMessage(
                currentUserId(session),
                dto.getReceiverId(),
                dto.getRequestId(),
                dto.getContent()
        );
    }

    @GetMapping("/{requestId}")
    public List<MessageResponseDTO> getChat(@PathVariable Long requestId, HttpSession session) {
        return chatService.getMessages(requestId, currentUserId(session));
    }

    private Long currentUserId(HttpSession session) {
        Object userId = session.getAttribute(SessionAuthConstants.AUTH_USER_ID);
        if (userId instanceof Long id) {
            return id;
        }
        if (userId instanceof Number number) {
            return number.longValue();
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
    }
}
