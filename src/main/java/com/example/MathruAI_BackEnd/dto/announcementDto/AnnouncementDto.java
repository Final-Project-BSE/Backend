package com.example.MathruAI_BackEnd.dto.announcementDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnnouncementDto {
    private int announcementId;
    private String title;
    private String content;
    private String category;
    private LocalDateTime createdAt;
    private boolean Active;
}