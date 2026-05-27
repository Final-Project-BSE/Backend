package com.example.MathruAI_BackEnd.dto.vaccination;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VaccinationSummaryDto {
    private long totalCards;
    private long upcomingVaccinations;
    private long todayVaccinations;
    private long missedPatients;
    private long completedPatients;
    private long pendingPatients;
}