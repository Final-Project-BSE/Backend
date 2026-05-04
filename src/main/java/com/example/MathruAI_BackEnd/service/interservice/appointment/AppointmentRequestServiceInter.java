package com.example.MathruAI_BackEnd.service.interservice.appointment;

import com.example.MathruAI_BackEnd.dto.appointment.AppointmentRequestCreateRequestDto;
import com.example.MathruAI_BackEnd.dto.appointment.AppointmentRequestResponseDto;

import java.util.List;

public interface AppointmentRequestServiceInter {

    AppointmentRequestResponseDto createRequest(Long midwifeId, Long userId, AppointmentRequestCreateRequestDto request);

    List<AppointmentRequestResponseDto> getRequestsForMidwife(Long midwifeId);

    AppointmentRequestResponseDto acceptRequest(Long midwifeId, String requestId);

    AppointmentRequestResponseDto declineRequest(Long midwifeId, String requestId);
}
