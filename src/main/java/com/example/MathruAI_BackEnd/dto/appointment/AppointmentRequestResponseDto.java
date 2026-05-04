package com.example.MathruAI_BackEnd.dto.appointment;

import com.example.MathruAI_BackEnd.entity.appointment.AppointmentRequestStatus;
import com.example.MathruAI_BackEnd.entity.appointment.AppointmentType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentRequestResponseDto {

    private String requestId;
    private Long midwifeId;
    private Long userId;
    private String firstName;
    private String lastName;
    private String userEmail;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate appointmentDate;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    private AppointmentType appointmentType;
    private String location;
    private String notes;
    private AppointmentRequestStatus status;

    private LocalDateTime requestedAt;
    private LocalDateTime updatedAt;
}
