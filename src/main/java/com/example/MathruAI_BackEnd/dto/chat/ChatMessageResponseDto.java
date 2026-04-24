package com.example.MathruAI_BackEnd.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponseDto {
    private Long id;
    private Long conversationId;
    private Long senderId;
    private String senderFirstName;
    private String senderLastName;
    private String senderEmail;
    private Set<?> senderRoles;
    private Long receiverId;
    private String content;
    private boolean read;
    private LocalDateTime createdAt;
}