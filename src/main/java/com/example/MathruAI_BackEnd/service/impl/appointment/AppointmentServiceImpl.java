package com.example.MathruAI_BackEnd.service.impl.appointment;

import com.example.MathruAI_BackEnd.dto.appointment.AppointmentCreateRequestDto;
import com.example.MathruAI_BackEnd.dto.appointment.AppointmentResponseDto;
import com.example.MathruAI_BackEnd.dto.appointment.BookedSlotsResponseDto;
import com.example.MathruAI_BackEnd.dto.appointment.UnavailableDatesResponseDto;
import com.example.MathruAI_BackEnd.dto.appointment.UpcomingAppointmentResponseDto;
import com.example.MathruAI_BackEnd.entity.Role;
import com.example.MathruAI_BackEnd.entity.User;
import com.example.MathruAI_BackEnd.entity.appointment.Appointment;
import com.example.MathruAI_BackEnd.entity.appointment.AppointmentStatus;
import com.example.MathruAI_BackEnd.repository.UserRepository;
import com.example.MathruAI_BackEnd.repository.appointment.AppointmentRepository;
import com.example.MathruAI_BackEnd.service.interservice.appointment.AppointmentServiceInter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentServiceImpl implements AppointmentServiceInter {

    private static final ZoneId APP_ZONE = ZoneId.of("Asia/Colombo");
    private static final int UNAVAILABLE_WINDOW_DAYS = 90;
    private static final List<LocalTime> SUPPORTED_SLOTS = List.of(
            LocalTime.of(8, 0),
            LocalTime.of(9, 0),
            LocalTime.of(10, 0),
            LocalTime.of(11, 0),
            LocalTime.of(13, 0),
            LocalTime.of(14, 0),
            LocalTime.of(15, 0),
            LocalTime.of(16, 0),
            LocalTime.of(17, 0)
    );

    private static final Set<Role> MOTHER_ROLES = Set.of(
            Role.HOPE_TO_PREGNANT_MOTHER,
            Role.PREGNANT_MOTHER,
            Role.POST_PREGNANT_MOTHER
    );

    private final UserRepository userRepository;
    private final AppointmentRepository appointmentRepository;

    @Override
    public AppointmentResponseDto createAppointment(Long midwifeId, Long userId, AppointmentCreateRequestDto request) {
        User midwife = getUserOrThrow(midwifeId, "Midwife not found.");
        User patient = getUserOrThrow(userId, "User not found.");

        validateMidwifePatientAssignment(midwife, patient);
        validateSupportedSlot(request.getStartTime());
        validateTimeRange(request.getStartTime(), request.getEndTime());
        validateDateTimeNotInPast(request.getAppointmentDate(), request.getStartTime());

        List<Appointment> conflictsWithLock = appointmentRepository.findByMidwifeAndAppointmentDateAndStartTimeAndStatus(
                midwife,
                request.getAppointmentDate(),
                request.getStartTime(),
                AppointmentStatus.SCHEDULED
        );

        if (!conflictsWithLock.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Selected time slot conflicts with an existing appointment.");
        }

        Appointment appointment = Appointment.builder()
                .midwife(midwife)
                .patient(patient)
                .appointmentDate(request.getAppointmentDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .appointmentType(request.getAppointmentType())
                .location(trim(request.getLocation()))
                .notes(trim(request.getNotes()))
                .status(AppointmentStatus.SCHEDULED)
                .build();

        return mapToResponse(appointmentRepository.save(appointment));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponseDto> getAppointmentsForUser(Long midwifeId, Long userId) {
        User midwife = getUserOrThrow(midwifeId, "Midwife not found.");
        User patient = getUserOrThrow(userId, "User not found.");

        validateMidwifePatientAssignment(midwife, patient);

        return appointmentRepository.findByMidwifeAndPatientOrderByAppointmentDateAscStartTimeAsc(midwife, patient)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AppointmentResponseDto cancelAppointment(Long midwifeId, Long userId, Long appointmentId) {
        User midwife = getUserOrThrow(midwifeId, "Midwife not found.");
        User user = getUserOrThrow(userId, "User not found.");

        validateMidwifePatientAssignment(midwife, user);

        Appointment appointment = appointmentRepository.findByIdAndMidwifeAndPatient(appointmentId, midwife, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found."));

        if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only SCHEDULED appointments can be cancelled.");
        }

        AppointmentResponseDto response = mapToResponse(appointment);
        appointmentRepository.delete(appointment);
        return response;
    }

    @Override
    public AppointmentResponseDto completeAppointment(Long midwifeId, Long userId, Long appointmentId) {
        return updateStatusForScheduledOnly(midwifeId, userId, appointmentId, AppointmentStatus.COMPLETED);
    }

    @Override
    public void deleteCompletedAppointment(Long midwifeId, Long userId, Long appointmentId) {
        User midwife = getUserOrThrow(midwifeId, "Midwife not found.");
        User user = getUserOrThrow(userId, "User not found.");

        validateMidwifePatientAssignment(midwife, user);

        Appointment appointment = appointmentRepository.findByIdAndMidwifeAndPatient(appointmentId, midwife, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found."));

        if (appointment.getStatus() != AppointmentStatus.COMPLETED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only COMPLETED appointments can be deleted.");
        }

        appointmentRepository.delete(appointment);
    }

    @Override
    @Transactional(readOnly = true)
    public UnavailableDatesResponseDto getUnavailableDates(Long midwifeId, Long userId) {
        User midwife = getUserOrThrow(midwifeId, "Midwife not found.");
        User user = getUserOrThrow(userId, "User not found.");

        validateMidwifePatientAssignment(midwife, user);

        LocalDate today = LocalDate.now(APP_ZONE);
        LocalDate last = today.plusDays(UNAVAILABLE_WINDOW_DAYS - 1L);

        List<Appointment> scheduledInWindow = appointmentRepository
            .findByMidwifeAndStatusAndAppointmentDateBetweenOrderByAppointmentDateAscStartTimeAsc(
                midwife,
                AppointmentStatus.SCHEDULED,
                today,
                last
            );

        Map<LocalDate, Set<LocalTime>> slotsByDate = new LinkedHashMap<>();
        for (Appointment appointment : scheduledInWindow) {
            slotsByDate
                .computeIfAbsent(appointment.getAppointmentDate(), d -> new HashSet<>())
                .add(appointment.getStartTime());
        }

        Map<LocalDate, String> reasonByDate = new LinkedHashMap<>();

        for (Map.Entry<LocalDate, Set<LocalTime>> entry : slotsByDate.entrySet()) {
            if (entry.getValue().size() >= SUPPORTED_SLOTS.size()) {
            reasonByDate.put(entry.getKey(), "All appointment time slots are filled for this day.");
            }
        }

        return UnavailableDatesResponseDto.builder()
            .dates(reasonByDate.keySet().stream().sorted().toList())
                .reasonByDate(reasonByDate)
                .build();
    }

        @Override
        @Transactional(readOnly = true)
        public BookedSlotsResponseDto getBookedSlotsForDate(Long midwifeId, Long userId, LocalDate date) {
        User midwife = getUserOrThrow(midwifeId, "Midwife not found.");
        User user = getUserOrThrow(userId, "User not found.");

        validateMidwifePatientAssignment(midwife, user);

        List<LocalTime> bookedSlots = appointmentRepository
            .findByMidwifeAndStatusAndAppointmentDateOrderByStartTimeAsc(
                midwife,
                AppointmentStatus.SCHEDULED,
                date
            )
            .stream()
            .map(Appointment::getStartTime)
            .distinct()
            .toList();

        return BookedSlotsResponseDto.builder()
            .date(date)
            .bookedSlots(bookedSlots)
            .build();
        }

    @Override
    @Transactional(readOnly = true)
    public List<UpcomingAppointmentResponseDto> getUpcomingAppointmentsForMidwife(Long midwifeId) {
        User midwife = getUserOrThrow(midwifeId, "Midwife not found.");

        if (!hasRole(midwife, Role.MIDWIFE)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not a midwife.");
        }

        LocalDateTime now = LocalDateTime.now(APP_ZONE);

        return appointmentRepository.findByMidwifeAndStatusOrderByAppointmentDateAscStartTimeAsc(
                        midwife,
                        AppointmentStatus.SCHEDULED
                )
                .stream()
                .filter(a -> a.getPatient() != null
                    && a.getPatient().getAssignedMidwife() != null
                    && midwifeId.equals(a.getPatient().getAssignedMidwife().getId()))
                .filter(a -> !LocalDateTime.of(a.getAppointmentDate(), a.getStartTime()).isBefore(now))
                .sorted(Comparator
                    .comparing(Appointment::getAppointmentDate)
                    .thenComparing(Appointment::getStartTime))
                .map(this::mapToUpcomingResponse)
                .collect(Collectors.toList());
    }

    private AppointmentResponseDto updateStatusForScheduledOnly(Long midwifeId, Long userId, Long appointmentId, AppointmentStatus target) {
        User midwife = getUserOrThrow(midwifeId, "Midwife not found.");
        User user = getUserOrThrow(userId, "User not found.");

        validateMidwifePatientAssignment(midwife, user);

        Appointment appointment = appointmentRepository.findByIdAndMidwifeAndPatient(appointmentId, midwife, user)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found."));

        if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only SCHEDULED appointments can be updated to this status.");
        }

        appointment.setStatus(target);

        return mapToResponse(appointmentRepository.save(appointment));
    }

    private void validateMidwifePatientAssignment(User midwife, User patient) {
        if (!hasRole(midwife, Role.MIDWIFE)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not a midwife.");
        }

        if (!hasAnyRole(patient, MOTHER_ROLES)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selected user is not a mother-side patient.");
        }

        if (patient.getAssignedMidwife() == null || !patient.getAssignedMidwife().getId().equals(midwife.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Patient is not assigned to this midwife.");
        }
    }

    private void validateDateTimeNotInPast(LocalDate appointmentDate, LocalTime appointmentTime) {
        LocalDateTime appointmentDateTime = LocalDateTime.of(appointmentDate, appointmentTime);
        if (appointmentDateTime.isBefore(LocalDateTime.now(APP_ZONE))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Appointment date and time cannot be in the past.");
        }
    }

    private void validateSupportedSlot(LocalTime startTime) {
        if (startTime == null || !SUPPORTED_SLOTS.contains(startTime)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid time slot. Allowed slots: 08:00, 09:00, 10:00, 11:00, 13:00, 14:00, 15:00, 16:00, 17:00."
            );
        }
    }

    private void validateTimeRange(LocalTime startTime, LocalTime endTime) {
        if (endTime != null && !endTime.isAfter(startTime)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "End time must be after start time.");
        }
    }

    private User getUserOrThrow(Long id, String message) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, message));
    }

    private boolean hasRole(User user, Role role) {
        return user.getRoles() != null && user.getRoles().contains(role);
    }

    private boolean hasAnyRole(User user, Set<Role> roles) {
        return user.getRoles() != null && user.getRoles().stream().anyMatch(roles::contains);
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }

    private AppointmentResponseDto mapToResponse(Appointment appointment) {
        return AppointmentResponseDto.builder()
                .id(appointment.getId())
                .midwifeId(appointment.getMidwife() != null ? appointment.getMidwife().getId() : null)
                .userId(appointment.getPatient() != null ? appointment.getPatient().getId() : null)
                .appointmentDate(appointment.getAppointmentDate())
                .startTime(appointment.getStartTime())
                .endTime(appointment.getEndTime())
                .appointmentType(appointment.getAppointmentType())
                .location(appointment.getLocation())
                .notes(appointment.getNotes())
                .status(appointment.getStatus())
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .build();
    }

    private UpcomingAppointmentResponseDto mapToUpcomingResponse(Appointment appointment) {
        String firstName = appointment.getPatient() != null ? appointment.getPatient().getFirstName() : null;
        String lastName = appointment.getPatient() != null ? appointment.getPatient().getLastName() : null;
        String userEmail = appointment.getPatient() != null ? appointment.getPatient().getEmail() : null;

        return UpcomingAppointmentResponseDto.builder()
            .appointmentId(appointment.getId())
                .userId(appointment.getPatient() != null ? appointment.getPatient().getId() : null)
            .userEmail(userEmail)
                .firstName(firstName)
                .lastName(lastName)
                .appointmentDate(appointment.getAppointmentDate())
                .startTime(appointment.getStartTime())
                .appointmentType(appointment.getAppointmentType())
                .location(appointment.getLocation())
                .status(appointment.getStatus())
                .build();
    }
}
