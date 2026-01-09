package com.example.MathruAI_BackEnd.dto.reproductiveDto.fertilitytracker;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FertilityResponseDto {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fertileWindowStart;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fertileWindowEnd;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate ovulationDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate nextPeriodDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate pregnancyTestDay;
}
