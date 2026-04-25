package com.example.MathruAI_BackEnd.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UnreadCountResponseDto {
    private long unreadCount;
}