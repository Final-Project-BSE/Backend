package com.example.MathruAI_BackEnd.service.impl.appointment;

import com.example.MathruAI_BackEnd.dto.appointment.AppointmentCreateRequestDto;
import com.example.MathruAI_BackEnd.dto.appointment.UnavailableDatesResponseDto;
import com.example.MathruAI_BackEnd.entity.Role;
import com.example.MathruAI_BackEnd.entity.User;
import com.example.MathruAI_BackEnd.entity.appointment.Appointment;
import com.example.MathruAI_BackEnd.entity.appointment.AppointmentStatus;
import com.example.MathruAI_BackEnd.entity.appointment.AppointmentType;
import com.example.MathruAI_BackEnd.repository.UserRepository;
import com.example.MathruAI_BackEnd.repository.appointment.AppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private AppointmentServiceImpl service;

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
                .email("user@example.com")
                .roles(Set.of(Role.PREGNANT_MOTHER))
                .assignedMidwife(midwife)
                .build();
    }

    @Test
    void createAppointment_rejectsUnsupportedSlot() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(midwife));
        when(userRepository.findById(2L)).thenReturn(Optional.of(patient));

        AppointmentCreateRequestDto request = AppointmentCreateRequestDto.builder()
                .appointmentDate(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(12, 0))
                .appointmentType(AppointmentType.ANTENATAL_CHECKUP)
                .location("Clinic")
                .build();

        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> service.createAppointment(1L, 2L, request)
        );

        assertEquals(400, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("Invalid time slot"));
    }

    @Test
    void getUnavailableDates_marksOnlyFullyBookedDate() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(midwife));
        when(userRepository.findById(2L)).thenReturn(Optional.of(patient));

        LocalDate base = LocalDate.now();
        LocalDate fullDate = base.plusDays(2);
        LocalDate partialDate = base.plusDays(3);

        List<Appointment> data = new ArrayList<>();

        data.add(buildScheduled(fullDate, LocalTime.of(8, 0)));
        data.add(buildScheduled(fullDate, LocalTime.of(9, 0)));
        data.add(buildScheduled(fullDate, LocalTime.of(10, 0)));
        data.add(buildScheduled(fullDate, LocalTime.of(11, 0)));
        data.add(buildScheduled(fullDate, LocalTime.of(13, 0)));
        data.add(buildScheduled(fullDate, LocalTime.of(14, 0)));
        data.add(buildScheduled(fullDate, LocalTime.of(15, 0)));
        data.add(buildScheduled(fullDate, LocalTime.of(16, 0)));
        data.add(buildScheduled(fullDate, LocalTime.of(17, 0)));

        data.add(buildScheduled(partialDate, LocalTime.of(8, 0)));
        data.add(buildScheduled(partialDate, LocalTime.of(9, 0)));

        when(appointmentRepository.findByMidwifeAndStatusAndAppointmentDateBetweenOrderByAppointmentDateAscStartTimeAsc(
                eq(midwife), eq(AppointmentStatus.SCHEDULED), any(LocalDate.class), any(LocalDate.class)
        )).thenReturn(data);

        UnavailableDatesResponseDto response = service.getUnavailableDates(1L, 2L);

        assertEquals(1, response.getDates().size());
        assertEquals(fullDate, response.getDates().get(0));
        assertEquals("All appointment time slots are filled for this day.", response.getReasonByDate().get(fullDate));
        assertFalse(response.getReasonByDate().containsKey(partialDate));
    }

    @Test
        void cancelAppointment_deletesAppointment() {
        Appointment appointment = Appointment.builder()
                .id(10L)
                .midwife(midwife)
                .patient(patient)
                .appointmentDate(LocalDate.now().plusDays(1))
                .startTime(LocalTime.of(8, 0))
                .appointmentType(AppointmentType.ANTENATAL_CHECKUP)
                .location("Clinic")
                .status(AppointmentStatus.SCHEDULED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(midwife));
        when(userRepository.findById(2L)).thenReturn(Optional.of(patient));
        when(appointmentRepository.findByIdAndMidwifeAndPatient(10L, midwife, patient)).thenReturn(Optional.of(appointment));
        service.cancelAppointment(1L, 2L, 10L);

        ArgumentCaptor<Appointment> captor = ArgumentCaptor.forClass(Appointment.class);
                verify(appointmentRepository).delete(captor.capture());
                verify(appointmentRepository, never()).save(any(Appointment.class));
                assertEquals(AppointmentStatus.SCHEDULED, captor.getValue().getStatus());
    }

    private Appointment buildScheduled(LocalDate date, LocalTime time) {
        return Appointment.builder()
                .midwife(midwife)
                .patient(patient)
                .appointmentDate(date)
                .startTime(time)
                .appointmentType(AppointmentType.ANTENATAL_CHECKUP)
                .location("Clinic")
                .status(AppointmentStatus.SCHEDULED)
                .build();
    }
}
