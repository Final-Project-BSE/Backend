package com.example.MathruAI_BackEnd.dto.triposhaDto;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TriposhaDTO {
    private Long patientId;
    private Long midwifeId;
    private LocalDate distributionDate;
    private int quantity;
    private String status;
    private LocalDate nextDueDate;
    private String notes;
}