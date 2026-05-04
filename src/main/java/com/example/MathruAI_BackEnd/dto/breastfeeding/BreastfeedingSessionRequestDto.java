package com.example.MathruAI_BackEnd.dto.breastfeeding;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BreastfeedingSessionRequestDto {

    private String feedingTime;
    private String side;
    private Integer durationMinutes;
    private Double milkAmountMl;
    private String notes;
}