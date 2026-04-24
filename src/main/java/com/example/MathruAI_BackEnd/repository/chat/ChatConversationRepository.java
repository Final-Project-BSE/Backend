package com.example.MathruAI_BackEnd.repository.chat;

import com.example.MathruAI_BackEnd.entity.chat.ChatConversation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatConversationRepository extends JpaRepository<ChatConversation, Long> {

    Optional<ChatConversation> findByMidwifeIdAndMotherId(Long midwifeId, Long motherId);

    List<ChatConversation> findByMidwifeIdOrderByLastMessageAtDesc(Long midwifeId);

    List<ChatConversation> findByMotherIdOrderByLastMessageAtDesc(Long motherId);
}