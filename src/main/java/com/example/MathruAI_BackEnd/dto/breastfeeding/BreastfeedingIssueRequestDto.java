package com.example.MathruAI_BackEnd.dto.breastfeeding;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BreastfeedingIssueRequestDto {

    private String issueType;
    private String description;
    private String severity;
    private String reportedAt;
    private boolean resolved;
    private String midwifeNotes;
}