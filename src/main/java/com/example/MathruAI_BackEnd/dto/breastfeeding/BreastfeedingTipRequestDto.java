package com.example.MathruAI_BackEnd.dto.breastfeeding;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BreastfeedingTipRequestDto {

    private String category;
    private String title;
    private String content;
    private boolean active;
}