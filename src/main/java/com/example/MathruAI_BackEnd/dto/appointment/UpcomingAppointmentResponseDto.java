package com.example.MathruAI_BackEnd.dto.appointment;

import com.example.MathruAI_BackEnd.entity.appointment.AppointmentStatus;
import com.example.MathruAI_BackEnd.entity.appointment.AppointmentType;
import com.fasterxml.jackson.annotation.JsonFormat;
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
public class UpcomingAppointmentResponseDto {
    private Long appointmentId;
    private Long userId;
    private String userEmail;
    private String firstName;
    private String lastName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate appointmentDate;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    private AppointmentType appointmentType;
    private String location;
    private AppointmentStatus status;
}
