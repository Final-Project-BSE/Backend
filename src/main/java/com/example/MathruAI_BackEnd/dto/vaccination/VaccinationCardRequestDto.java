package com.example.MathruAI_BackEnd.dto.vaccination;

import com.example.MathruAI_BackEnd.entity.vaccination.VaccinationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class VaccinationCardRequestDto {

    @NotBlank(message = "Vaccine name is required.")
    @Size(max = 150)
    private String vaccineName;

    @Size(max = 120)
    private String vaccineType;

    @Size(max = 80)
    private String dose;

    @NotNull(message = "Due date is required.")
    private LocalDate dueDate;

    private VaccinationStatus status;

    @Size(max = 1000)
    private String midwifeNote;

    private LocalDate completedDate;

    private LocalDate vaccinationInjectionDate;

    @Size(max = 255)
    private String location;
}