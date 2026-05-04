package com.example.MathruAI_BackEnd.dto.appointment;

import com.example.MathruAI_BackEnd.entity.appointment.AppointmentType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentCreateRequestDto {

    @NotNull(message = "Appointment date is required.")
    @FutureOrPresent(message = "Appointment date cannot be in the past.")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate appointmentDate;

    @NotNull(message = "Start time is required.")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    @NotNull(message = "Appointment type is required.")
    private AppointmentType appointmentType;

    @NotBlank(message = "Location is required.")
    @Size(max = 255, message = "Location cannot exceed 255 characters.")
    private String location;

    @Size(max = 2000, message = "Notes cannot exceed 2000 characters.")
    private String notes;
}
