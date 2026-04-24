package com.example.MathruAI_BackEnd.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatConversationResponseDto {
    private Long id;
    private ChatParticipantDto otherUser;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
    private long unreadCount;
}