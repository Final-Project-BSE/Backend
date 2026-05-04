package com.example.MathruAI_BackEnd.dto.appointment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnavailableDatesResponseDto {

    @JsonFormat(pattern = "yyyy-MM-dd")
    private List<LocalDate> dates;

    private Map<LocalDate, String> reasonByDate;
}
