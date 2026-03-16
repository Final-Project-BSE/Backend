package com.example.MathruAI_BackEnd.dto.healthrecords;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthCategoryResponseDto {
    private UUID id;
    private String slug;
    private String name;
    private String icon;
    private String colorClass;
    private long recordCount;
}
