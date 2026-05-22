package com.example.MathruAI_BackEnd.service.impl.appointment;

import com.example.MathruAI_BackEnd.dto.appointment.AppointmentRequestCreateRequestDto;
import com.example.MathruAI_BackEnd.dto.appointment.AppointmentRequestResponseDto;
import com.example.MathruAI_BackEnd.entity.Role;
import com.example.MathruAI_BackEnd.entity.User;
import com.example.MathruAI_BackEnd.entity.appointment.Appointment;
import com.example.MathruAI_BackEnd.entity.appointment.AppointmentRequest;
import com.example.MathruAI_BackEnd.entity.appointment.AppointmentRequestStatus;
import com.example.MathruAI_BackEnd.entity.appointment.AppointmentStatus;
import com.example.MathruAI_BackEnd.repository.UserRepository;
import com.example.MathruAI_BackEnd.repository.appointment.AppointmentRepository;
import com.example.MathruAI_BackEnd.repository.appointment.AppointmentRequestRepository;
import com.example.MathruAI_BackEnd.service.interservice.appointment.AppointmentRequestServiceInter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentRequestServiceImpl implements AppointmentRequestServiceInter {

    private static final ZoneId APP_ZONE = ZoneId.of("Asia/Colombo");

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
    private final AppointmentRequestRepository appointmentRequestRepository;
    private final AppointmentEmailService appointmentEmailService;

    @Override
    public AppointmentRequestResponseDto createRequest(
            Long midwifeId,
            Long userId,
            AppointmentRequestCreateRequestDto request
    ) {
        User midwife = getUserOrThrow(midwifeId, "Midwife not found.");
        User patient = getUserOrThrow(userId, "User not found.");

        validateMidwifePatientAssignment(midwife, patient);
        validateSupportedSlot(request.getStartTime());
        validateTimeRange(request.getStartTime(), request.getEndTime());
        validateDateTimeNotInPast(request.getAppointmentDate(), request.getStartTime());

        List<Appointment> conflicts =
                appointmentRepository.findByMidwifeAndAppointmentDateAndStartTimeAndStatus(
                        midwife,
                        request.getAppointmentDate(),
                        request.getStartTime(),
                        AppointmentStatus.SCHEDULED
                );

        if (!conflicts.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Selected time slot is already booked."
            );
        }

        AppointmentRequest appointmentRequest = AppointmentRequest.builder()
                .requestId(UUID.randomUUID().toString())
                .midwife(midwife)
                .patient(patient)
                .appointmentDate(request.getAppointmentDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .appointmentType(request.getAppointmentType())
                .location(trim(request.getLocation()))
                .notes(trim(request.getNotes()))
                .status(AppointmentRequestStatus.PENDING)
                .build();

        AppointmentRequest saved = appointmentRequestRepository.save(appointmentRequest);

        appointmentEmailService.sendAppointmentRequestCreatedEmails(saved);

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentRequestResponseDto> getRequestsForMidwife(Long midwifeId) {
        User midwife = getUserOrThrow(midwifeId, "Midwife not found.");

        if (!hasRole(midwife, Role.MIDWIFE)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not a midwife.");
        }

        return appointmentRequestRepository
                .findByMidwifeOrderByRequestedAtDesc(midwife)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public AppointmentRequestResponseDto acceptRequest(Long midwifeId, String requestId) {
        User midwife = getUserOrThrow(midwifeId, "Midwife not found.");

        if (!hasRole(midwife, Role.MIDWIFE)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not a midwife.");
        }

        AppointmentRequest appointmentRequest = appointmentRequestRepository
                .findByRequestIdAndMidwife(requestId, midwife)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Appointment request not found."
                ));

        if (appointmentRequest.getStatus() != AppointmentRequestStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only PENDING requests can be accepted."
            );
        }

        validateDateTimeNotInPast(
                appointmentRequest.getAppointmentDate(),
                appointmentRequest.getStartTime()
        );

        List<Appointment> conflictsWithLock =
                appointmentRepository.findByMidwifeAndAppointmentDateAndStartTimeAndStatus(
                        midwife,
                        appointmentRequest.getAppointmentDate(),
                        appointmentRequest.getStartTime(),
                        AppointmentStatus.SCHEDULED
                );

        if (!conflictsWithLock.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Selected time slot is already booked."
            );
        }

        Appointment appointment = Appointment.builder()
                .midwife(midwife)
                .patient(appointmentRequest.getPatient())
                .appointmentDate(appointmentRequest.getAppointmentDate())
                .startTime(appointmentRequest.getStartTime())
                .endTime(appointmentRequest.getEndTime())
                .appointmentType(appointmentRequest.getAppointmentType())
                .location(trim(appointmentRequest.getLocation()))
                .notes(trim(appointmentRequest.getNotes()))
                .status(AppointmentStatus.SCHEDULED)
                .build();

        appointmentRepository.save(appointment);

        appointmentRequest.setStatus(AppointmentRequestStatus.ACCEPTED);

        AppointmentRequest savedRequest = appointmentRequestRepository.save(appointmentRequest);

        appointmentEmailService.sendAppointmentRequestAcceptedEmails(savedRequest);

        return mapToResponse(savedRequest);
    }

    @Override
    public AppointmentRequestResponseDto declineRequest(Long midwifeId, String requestId) {
        User midwife = getUserOrThrow(midwifeId, "Midwife not found.");

        if (!hasRole(midwife, Role.MIDWIFE)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not a midwife.");
        }

        AppointmentRequest appointmentRequest = appointmentRequestRepository
                .findByRequestIdAndMidwife(requestId, midwife)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Appointment request not found."
                ));

        if (appointmentRequest.getStatus() != AppointmentRequestStatus.PENDING) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only PENDING requests can be declined."
            );
        }

        appointmentRequest.setStatus(AppointmentRequestStatus.DECLINED);

        AppointmentRequest savedRequest = appointmentRequestRepository.save(appointmentRequest);

        appointmentEmailService.sendAppointmentRequestDeclinedEmails(savedRequest);

        return mapToResponse(savedRequest);
    }

    private User getUserOrThrow(Long id, String message) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, message));
    }

    private void validateMidwifePatientAssignment(User midwife, User patient) {
        if (!hasRole(midwife, Role.MIDWIFE)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not a midwife.");
        }

        if (!hasAnyRole(patient, MOTHER_ROLES)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Selected user is not a mother-side patient."
            );
        }

        if (patient.getAssignedMidwife() == null
                || !patient.getAssignedMidwife().getId().equals(midwife.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Patient is not assigned to this midwife."
            );
        }
    }

    private boolean hasRole(User user, Role role) {
        return user.getRoles() != null && user.getRoles().contains(role);
    }

    private boolean hasAnyRole(User user, Set<Role> roles) {
        return user.getRoles() != null
                && user.getRoles().stream().anyMatch(roles::contains);
    }

    private void validateSupportedSlot(LocalTime startTime) {
        if (startTime == null || !SUPPORTED_SLOTS.contains(startTime)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Unsupported start time. Allowed slots: 08:00, 09:00, 10:00, 11:00, 13:00, 14:00, 15:00, 16:00, 17:00."
            );
        }
    }

    private void validateTimeRange(LocalTime start, LocalTime end) {
        if (end != null && !end.isAfter(start)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "End time must be after start time."
            );
        }
    }

    private void validateDateTimeNotInPast(LocalDate date, LocalTime startTime) {
        LocalDateTime now = LocalDateTime.now(APP_ZONE);
        LocalDateTime appointmentDateTime = LocalDateTime.of(date, startTime);

        if (appointmentDateTime.isBefore(now)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot create or process appointments in the past."
            );
        }
    }

    private String trim(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();

        return trimmed.isEmpty() ? null : trimmed;
    }

    private AppointmentRequestResponseDto mapToResponse(AppointmentRequest request) {
        User patient = request.getPatient();

        return AppointmentRequestResponseDto.builder()
                .requestId(request.getRequestId())
                .midwifeId(request.getMidwife() != null
                        ? request.getMidwife().getId()
                        : null)
                .userId(patient != null
                        ? patient.getId()
                        : null)
                .firstName(patient != null
                        ? patient.getFirstName()
                        : null)
                .lastName(patient != null
                        ? patient.getLastName()
                        : null)
                .userEmail(patient != null
                        ? patient.getEmail()
                        : null)
                .appointmentDate(request.getAppointmentDate())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .appointmentType(request.getAppointmentType())
                .location(request.getLocation())
                .notes(request.getNotes())
                .status(request.getStatus())
                .requestedAt(request.getRequestedAt())
                .updatedAt(request.getUpdatedAt())
                .build();
    }
}