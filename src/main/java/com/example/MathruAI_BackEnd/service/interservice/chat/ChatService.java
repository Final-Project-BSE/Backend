package com.example.MathruAI_BackEnd.service.interservice.chat;

import com.example.MathruAI_BackEnd.dto.chat.ChatConversationResponseDto;
import com.example.MathruAI_BackEnd.dto.chat.ChatMessageResponseDto;

import java.util.List;

public interface ChatService {
    List<ChatConversationResponseDto> getMyConversations(Long currentUserId);
    ChatConversationResponseDto getOrCreateConversation(Long currentUserId, Long targetUserId);
    List<ChatMessageResponseDto> getMessages(Long currentUserId, Long conversationId);
    ChatMessageResponseDto sendMessage(Long currentUserId, Long conversationId, String content);
    void markConversationAsRead(Long currentUserId, Long conversationId);
    long getUnreadCount(Long currentUserId);
}