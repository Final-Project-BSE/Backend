package com.example.MathruAI_BackEnd.dto.breastfeeding;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BreastfeedingSessionResponseDto {

    private UUID id;
    private String feedingTime;
    private String side;
    private Integer durationMinutes;
    private Double milkAmountMl;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}