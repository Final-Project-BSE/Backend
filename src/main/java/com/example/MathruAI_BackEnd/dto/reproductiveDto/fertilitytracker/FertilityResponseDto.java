package com.example.MathruAI_BackEnd.dto.reproductiveDto.fertilitytracker;

import java.time.LocalDate;

public record FertilityResponseDto(
        LocalDate fertileWindowStart,
        LocalDate fertileWindowEnd,
        LocalDate ovulationDate,
        LocalDate nextPeriodDate,
        LocalDate pregnancyTestDay,
        LocalDate safeStart1,
        LocalDate safeEnd1,
        LocalDate safeStart2,
        LocalDate safeEnd2,
        LocalDate lastPeriodDate,
        Integer averageCycleLength
) {
}