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
public class BreastfeedingIssueResponseDto {

    private UUID id;
    private String issueType;
    private String description;
    private String severity;
    private LocalDateTime reportedAt;
    private boolean resolved;
    private String midwifeNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}