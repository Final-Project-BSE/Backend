package com.example.MathruAI_BackEnd.controller.chat;

import com.example.MathruAI_BackEnd.dto.chat.*;
import com.example.MathruAI_BackEnd.service.interservice.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/conversations/{currentUserId}")
    public ResponseEntity<List<ChatConversationResponseDto>> getMyConversations(
            @PathVariable Long currentUserId
    ) {
        return ResponseEntity.ok(chatService.getMyConversations(currentUserId));
    }

    @PostMapping("/conversations/open/{currentUserId}")
    public ResponseEntity<ChatConversationResponseDto> getOrCreateConversation(
            @PathVariable Long currentUserId,
            @RequestBody ChatOpenConversationRequestDto request
    ) {
        return ResponseEntity.ok(
                chatService.getOrCreateConversation(currentUserId, request.getTargetUserId())
        );
    }

    @GetMapping("/conversations/{conversationId}/messages/{currentUserId}")
    public ResponseEntity<List<ChatMessageResponseDto>> getMessages(
            @PathVariable Long conversationId,
            @PathVariable Long currentUserId
    ) {
        return ResponseEntity.ok(chatService.getMessages(currentUserId, conversationId));
    }

    @PatchMapping("/conversations/{conversationId}/read/{currentUserId}")
    public ResponseEntity<String> markConversationAsRead(
            @PathVariable Long conversationId,
            @PathVariable Long currentUserId
    ) {
        chatService.markConversationAsRead(currentUserId, conversationId);
        return ResponseEntity.ok("Conversation marked as read.");
    }

    @GetMapping("/unread/{currentUserId}")
    public ResponseEntity<UnreadCountResponseDto> getUnreadCount(
            @PathVariable Long currentUserId
    ) {
        return ResponseEntity.ok(new UnreadCountResponseDto(chatService.getUnreadCount(currentUserId)));
    }
}