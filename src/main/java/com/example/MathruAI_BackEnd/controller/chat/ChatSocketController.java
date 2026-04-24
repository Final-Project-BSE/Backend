package com.example.MathruAI_BackEnd.controller.chat;

import com.example.MathruAI_BackEnd.dto.chat.ChatMessageResponseDto;
import com.example.MathruAI_BackEnd.dto.chat.ChatSendMessageRequestDto;
import com.example.MathruAI_BackEnd.entity.User;
import com.example.MathruAI_BackEnd.repository.UserRepository;
import com.example.MathruAI_BackEnd.service.interservice.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatSocketController {

    private final ChatService chatService;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(
            ChatSendMessageRequestDto request,
            SimpMessageHeaderAccessor headerAccessor
    ) {
        Principal principal = headerAccessor.getUser();

        if (principal == null || principal.getName() == null) {
            throw new RuntimeException("Unauthorized socket user.");
        }

        User sender = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found for socket principal."));

        ChatMessageResponseDto saved = chatService.sendMessage(
                sender.getId(),
                request.getConversationId(),
                request.getContent()
        );

        messagingTemplate.convertAndSend(
                "/topic/chat/" + saved.getConversationId(),
                saved
        );

        messagingTemplate.convertAndSend(
                "/topic/chat-unread/" + saved.getReceiverId(),
                chatService.getUnreadCount(saved.getReceiverId())
        );
    }
}