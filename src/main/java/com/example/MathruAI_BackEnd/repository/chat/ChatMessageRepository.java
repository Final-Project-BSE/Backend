package com.example.MathruAI_BackEnd.repository.chat;

import com.example.MathruAI_BackEnd.entity.chat.LiveChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<LiveChatMessage, Long> {

    List<LiveChatMessage> findByConversationIdOrderByCreatedAtAsc(Long conversationId);

    long countByReceiverIdAndIsReadFalse(Long receiverId);

    long countByConversationIdAndReceiverIdAndIsReadFalse(Long conversationId, Long receiverId);

    List<LiveChatMessage> findByConversationIdAndReceiverIdAndIsReadFalse(Long conversationId, Long receiverId);
}