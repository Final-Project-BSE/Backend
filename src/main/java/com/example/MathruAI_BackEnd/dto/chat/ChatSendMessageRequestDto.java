package com.example.MathruAI_BackEnd.dto.chat;

import lombok.Data;

@Data
public class ChatSendMessageRequestDto {
    private Long conversationId;
    private String content;
}