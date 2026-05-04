package com.example.MathruAI_BackEnd.service.interservice.appointment;

import com.example.MathruAI_BackEnd.dto.appointment.AppointmentCreateRequestDto;
import com.example.MathruAI_BackEnd.dto.appointment.AppointmentResponseDto;
import com.example.MathruAI_BackEnd.dto.appointment.BookedSlotsResponseDto;
import com.example.MathruAI_BackEnd.dto.appointment.UnavailableDatesResponseDto;
import com.example.MathruAI_BackEnd.dto.appointment.UpcomingAppointmentResponseDto;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentServiceInter {

    AppointmentResponseDto createAppointment(Long midwifeId, Long userId, AppointmentCreateRequestDto request);

    List<AppointmentResponseDto> getAppointmentsForUser(Long midwifeId, Long userId);

    AppointmentResponseDto cancelAppointment(Long midwifeId, Long userId, Long appointmentId);

    AppointmentResponseDto completeAppointment(Long midwifeId, Long userId, Long appointmentId);

    void deleteCompletedAppointment(Long midwifeId, Long userId, Long appointmentId);

    UnavailableDatesResponseDto getUnavailableDates(Long midwifeId, Long userId);

    BookedSlotsResponseDto getBookedSlotsForDate(Long midwifeId, Long userId, LocalDate date);

    List<UpcomingAppointmentResponseDto> getUpcomingAppointmentsForMidwife(Long midwifeId);
}
