package com.example.MathruAI_BackEnd.controller.appointment;

import com.example.MathruAI_BackEnd.dto.appointment.AppointmentCreateRequestDto;
import com.example.MathruAI_BackEnd.dto.appointment.AppointmentResponseDto;
import com.example.MathruAI_BackEnd.dto.appointment.BookedSlotsResponseDto;
import com.example.MathruAI_BackEnd.dto.appointment.UnavailableDatesResponseDto;
import com.example.MathruAI_BackEnd.dto.appointment.UpcomingAppointmentResponseDto;
import com.example.MathruAI_BackEnd.service.interservice.appointment.AppointmentServiceInter;
import com.example.MathruAI_BackEnd.service.interservice.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointments", description = "Manage midwife-patient appointments")
public class AppointmentController {

    private final AppointmentServiceInter appointmentService;
    private final UserService userService;

    private void validateAuthenticatedUser(Authentication authentication, Long midwifeId, Long userId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required.");
        }
        String email = authentication.getName();
            Long authenticatedUserId = userService.getByEmail(email).getId();
        
        if (!authenticatedUserId.equals(midwifeId) && !authenticatedUserId.equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to access this resource.");
        }
    }

    private void validateMidwifeAuthentication(Authentication authentication, Long midwifeId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required.");
        }
        String email = authentication.getName();
            Long authenticatedUserId = userService.getByEmail(email).getId();
        
        if (!authenticatedUserId.equals(midwifeId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to access this resource.");
        }
    }

    @GetMapping("/midwife/{midwifeId}/user/{userId}")
    @Operation(summary = "Get user appointments", description = "Returns all appointments for the user under this midwife")
    public ResponseEntity<List<AppointmentResponseDto>> getAppointmentsForUser(
            @PathVariable Long midwifeId,
            @PathVariable Long userId,
            Authentication authentication
    ) {
        validateAuthenticatedUser(authentication, midwifeId, userId);
        return ResponseEntity.ok(appointmentService.getAppointmentsForUser(midwifeId, userId));
    }

    @PostMapping("/midwife/{midwifeId}/user/{userId}")
    @Operation(summary = "Create appointment", description = "Creates appointment for the user under this midwife")
    public ResponseEntity<AppointmentResponseDto> createAppointment(
            @PathVariable Long midwifeId,
            @PathVariable Long userId,
            @Valid @RequestBody AppointmentCreateRequestDto request,
            Authentication authentication
    ) {
        validateAuthenticatedUser(authentication, midwifeId, userId);
        return ResponseEntity.ok(appointmentService.createAppointment(midwifeId, userId, request));
    }

    @PatchMapping("/midwife/{midwifeId}/user/{userId}/{appointmentId}/cancel")
    @Operation(summary = "Cancel appointment", description = "Marks appointment status as CANCELED")
    public ResponseEntity<AppointmentResponseDto> cancelAppointment(
            @PathVariable Long midwifeId,
            @PathVariable Long userId,
            @PathVariable Long appointmentId,
            Authentication authentication
    ) {
        validateAuthenticatedUser(authentication, midwifeId, userId);
        return ResponseEntity.ok(appointmentService.cancelAppointment(midwifeId, userId, appointmentId));
    }

    @PatchMapping("/midwife/{midwifeId}/user/{userId}/{appointmentId}/complete")
    @Operation(summary = "Complete appointment", description = "Marks appointment status as COMPLETED")
    public ResponseEntity<AppointmentResponseDto> completeAppointment(
            @PathVariable Long midwifeId,
            @PathVariable Long userId,
            @PathVariable Long appointmentId,
            Authentication authentication
    ) {
        validateAuthenticatedUser(authentication, midwifeId, userId);
        return ResponseEntity.ok(appointmentService.completeAppointment(midwifeId, userId, appointmentId));
    }

    @DeleteMapping("/midwife/{midwifeId}/user/{userId}/{appointmentId}")
    @Operation(summary = "Delete completed appointment", description = "Delete one completed appointment by id")
    public ResponseEntity<Void> deleteCompletedAppointment(
            @PathVariable Long midwifeId,
            @PathVariable Long userId,
            @PathVariable Long appointmentId,
            Authentication authentication
    ) {
        validateAuthenticatedUser(authentication, midwifeId, userId);
        appointmentService.deleteCompletedAppointment(midwifeId, userId, appointmentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/midwife/{midwifeId}/user/{userId}/unavailable-dates")
    @Operation(summary = "Get unavailable dates", description = "Returns blocked dates for this midwife-user pair")
    public ResponseEntity<UnavailableDatesResponseDto> getUnavailableDates(
            @PathVariable Long midwifeId,
            @PathVariable Long userId,
            Authentication authentication
    ) {
        validateAuthenticatedUser(authentication, midwifeId, userId);
        return ResponseEntity.ok(appointmentService.getUnavailableDates(midwifeId, userId));
    }

    @GetMapping("/midwife/{midwifeId}/user/{userId}/booked-slots")
    @Operation(summary = "Get booked slots for date", description = "Returns booked slots for a date across all midwife patients")
    public ResponseEntity<BookedSlotsResponseDto> getBookedSlotsForDate(
            @PathVariable Long midwifeId,
            @PathVariable Long userId,
            @RequestParam LocalDate date,
            Authentication authentication
    ) {
        validateAuthenticatedUser(authentication, midwifeId, userId);
        return ResponseEntity.ok(appointmentService.getBookedSlotsForDate(midwifeId, userId, date));
    }

    @GetMapping("/midwife/{midwifeId}/upcoming")
    @Operation(summary = "Get upcoming appointments", description = "Return all upcoming scheduled appointments across assigned users")
    public ResponseEntity<List<UpcomingAppointmentResponseDto>> getUpcomingAppointmentsForMidwife(
            @PathVariable Long midwifeId,
            Authentication authentication
    ) {
        validateMidwifeAuthentication(authentication, midwifeId);
        return ResponseEntity.ok(appointmentService.getUpcomingAppointmentsForMidwife(midwifeId));
    }
}
