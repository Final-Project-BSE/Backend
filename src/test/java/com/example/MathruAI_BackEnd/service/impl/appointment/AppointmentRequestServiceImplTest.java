package com.example.MathruAI_BackEnd.service.impl.appointment;

import com.example.MathruAI_BackEnd.dto.appointment.AppointmentRequestCreateRequestDto;
import com.example.MathruAI_BackEnd.dto.appointment.AppointmentRequestResponseDto;
import com.example.MathruAI_BackEnd.entity.Role;
import com.example.MathruAI_BackEnd.entity.User;
import com.example.MathruAI_BackEnd.entity.appointment.Appointment;
import com.example.MathruAI_BackEnd.entity.appointment.AppointmentRequest;
import com.example.MathruAI_BackEnd.entity.appointment.AppointmentRequestStatus;
import com.example.MathruAI_BackEnd.entity.appointment.AppointmentStatus;
import com.example.MathruAI_BackEnd.entity.appointment.AppointmentType;
import com.example.MathruAI_BackEnd.repository.UserRepository;
import com.example.MathruAI_BackEnd.repository.appointment.AppointmentRepository;
import com.example.MathruAI_BackEnd.repository.appointment.AppointmentRequestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AppointmentRequestServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private AppointmentRequestRepository appointmentRequestRepository;

    @InjectMocks
    private AppointmentRequestServiceImpl service;

    private User midwife;
    private User patient;

    @BeforeEach
    void setUp() {
        midwife = User.builder()
            .id(1L)
            .email("midwife@example.com")
            .roles(Set.of(Role.MIDWIFE))
            .build();

        patient = User.builder()
            .id(2L)
            .firstName("Jane")
            .lastName("Doe")
            .email("mother@example.com")
            .roles(Set.of(Role.PREGNANT_MOTHER))
            .assignedMidwife(midwife)
            .build();
    }

    @Test
    void createRequest_createsPendingRequest() {
        AppointmentRequestCreateRequestDto request = AppointmentRequestCreateRequestDto.builder()
            .appointmentDate(LocalDate.now().plusDays(1))
            .startTime(LocalTime.of(9, 0))
            .endTime(LocalTime.of(10, 0))
            .appointmentType(AppointmentType.ANTENATAL_CHECKUP)
            .location("Clinic")
            .notes("Need consultation")
            .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(midwife));
        when(userRepository.findById(2L)).thenReturn(Optional.of(patient));
        when(appointmentRepository.findByMidwifeAndAppointmentDateAndStartTimeAndStatus(
            midwife,
            request.getAppointmentDate(),
            request.getStartTime(),
            AppointmentStatus.SCHEDULED
        )).thenReturn(List.of());
        when(appointmentRequestRepository.save(any(AppointmentRequest.class))).thenAnswer(inv -> inv.getArgument(0));

        AppointmentRequestResponseDto response = service.createRequest(1L, 2L, request);

        assertEquals(AppointmentRequestStatus.PENDING, response.getStatus());
        assertEquals(1L, response.getMidwifeId());
        assertEquals(2L, response.getUserId());
        assertNotNull(response.getRequestId());
    }

    @Test
    void acceptRequest_marksAcceptedAndCreatesAppointment() {
        AppointmentRequest appointmentRequest = AppointmentRequest.builder()
            .requestId("req-1")
            .midwife(midwife)
            .patient(patient)
            .appointmentDate(LocalDate.now().plusDays(1))
            .startTime(LocalTime.of(10, 0))
            .endTime(LocalTime.of(11, 0))
            .appointmentType(AppointmentType.CLINIC_VISIT)
            .location("MOH Clinic")
            .notes("Requested by mother")
            .status(AppointmentRequestStatus.PENDING)
            .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(midwife));
        when(appointmentRequestRepository.findByRequestIdAndMidwife("req-1", midwife)).thenReturn(Optional.of(appointmentRequest));
        when(appointmentRepository.findByMidwifeAndAppointmentDateAndStartTimeAndStatus(
            midwife,
            appointmentRequest.getAppointmentDate(),
            appointmentRequest.getStartTime(),
            AppointmentStatus.SCHEDULED
        )).thenReturn(List.of());
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(inv -> inv.getArgument(0));
        when(appointmentRequestRepository.save(any(AppointmentRequest.class))).thenAnswer(inv -> inv.getArgument(0));

        AppointmentRequestResponseDto response = service.acceptRequest(1L, "req-1");

        assertEquals(AppointmentRequestStatus.ACCEPTED, response.getStatus());
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void acceptRequest_rejectsWhenSlotAlreadyBooked() {
        AppointmentRequest appointmentRequest = AppointmentRequest.builder()
            .requestId("req-2")
            .midwife(midwife)
            .patient(patient)
            .appointmentDate(LocalDate.now().plusDays(1))
            .startTime(LocalTime.of(11, 0))
            .appointmentType(AppointmentType.ANTENATAL_CHECKUP)
            .location("Clinic")
            .status(AppointmentRequestStatus.PENDING)
            .build();

        Appointment existing = Appointment.builder()
            .id(44L)
            .midwife(midwife)
            .patient(patient)
            .appointmentDate(appointmentRequest.getAppointmentDate())
            .startTime(appointmentRequest.getStartTime())
            .status(AppointmentStatus.SCHEDULED)
            .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(midwife));
        when(appointmentRequestRepository.findByRequestIdAndMidwife("req-2", midwife)).thenReturn(Optional.of(appointmentRequest));
        when(appointmentRepository.findByMidwifeAndAppointmentDateAndStartTimeAndStatus(
            midwife,
            appointmentRequest.getAppointmentDate(),
            appointmentRequest.getStartTime(),
            AppointmentStatus.SCHEDULED
        )).thenReturn(List.of(existing));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> service.acceptRequest(1L, "req-2"));

        assertEquals(409, ex.getStatusCode().value());
        assertEquals("Selected time slot is already booked.", ex.getReason());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void declineRequest_marksDeclined() {
        AppointmentRequest appointmentRequest = AppointmentRequest.builder()
            .requestId("req-3")
            .midwife(midwife)
            .patient(patient)
            .appointmentDate(LocalDate.now().plusDays(1))
            .startTime(LocalTime.of(13, 0))
            .appointmentType(AppointmentType.HOME_VISIT)
            .location("Home")
            .status(AppointmentRequestStatus.PENDING)
            .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(midwife));
        when(appointmentRequestRepository.findByRequestIdAndMidwife("req-3", midwife)).thenReturn(Optional.of(appointmentRequest));
        when(appointmentRequestRepository.save(any(AppointmentRequest.class))).thenAnswer(inv -> inv.getArgument(0));

        AppointmentRequestResponseDto response = service.declineRequest(1L, "req-3");

        ArgumentCaptor<AppointmentRequest> captor = ArgumentCaptor.forClass(AppointmentRequest.class);
        verify(appointmentRequestRepository).save(captor.capture());
        assertEquals(AppointmentRequestStatus.DECLINED, captor.getValue().getStatus());
        assertEquals(AppointmentRequestStatus.DECLINED, response.getStatus());
    }
}
