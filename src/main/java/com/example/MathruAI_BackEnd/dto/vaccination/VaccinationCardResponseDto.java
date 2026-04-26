package com.example.MathruAI_BackEnd.dto.vaccination;

import com.example.MathruAI_BackEnd.entity.vaccination.VaccinationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class VaccinationCardResponseDto {
    private Long id;
    private Long midwifeId;
    private Long patientId;
    private String patientName;
    private String patientEmail;
    private String vaccineName;
    private String vaccineType;
    private String dose;
    private LocalDate dueDate;
    private VaccinationStatus status;
    private String midwifeNote;
    private LocalDate completedDate;
    private LocalDate vaccinationInjectionDate;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}