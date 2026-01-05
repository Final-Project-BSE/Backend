package com.example.MathruAI_BackEnd.dto.reproductiveDto.fertilitytracker;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FertilityRequestDto {

    private Long userId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate lastPeriodDate;

    private int averageCycleLength;
}
