package com.example.MathruAI_BackEnd.controller.appointment;

import com.example.MathruAI_BackEnd.dto.appointment.AppointmentRequestCreateRequestDto;
import com.example.MathruAI_BackEnd.dto.appointment.AppointmentRequestResponseDto;
import com.example.MathruAI_BackEnd.entity.User;
import com.example.MathruAI_BackEnd.repository.UserRepository;
import com.example.MathruAI_BackEnd.service.interservice.appointment.AppointmentRequestServiceInter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/appointment-requests")
@RequiredArgsConstructor
@Tag(name = "Appointment Requests", description = "Manage appointment requests between mothers and midwives")
public class AppointmentRequestController {

    private final AppointmentRequestServiceInter appointmentRequestService;
    private final UserRepository userRepository;

    @PostMapping("/midwife/{midwifeId}/user/{userId}")
    @Operation(summary = "Create appointment request", description = "Mother creates a new appointment request for a midwife")
    public ResponseEntity<AppointmentRequestResponseDto> createRequest(
        @PathVariable Long midwifeId,
        @PathVariable Long userId,
        @Valid @RequestBody AppointmentRequestCreateRequestDto request,
        Authentication authentication
    ) {
        validateAuthenticatedUser(authentication, midwifeId, userId);
        return ResponseEntity.ok(appointmentRequestService.createRequest(midwifeId, userId, request));
    }

    @GetMapping("/midwife/{midwifeId}")
    @Operation(summary = "Get requests for midwife", description = "Get all appointment requests for a midwife")
    public ResponseEntity<List<AppointmentRequestResponseDto>> getRequestsForMidwife(
        @PathVariable Long midwifeId,
        Authentication authentication
    ) {
        validateMidwifeAuthentication(authentication, midwifeId);
        return ResponseEntity.ok(appointmentRequestService.getRequestsForMidwife(midwifeId));
    }

    @PatchMapping("/midwife/{midwifeId}/{requestId}/accept")
    @Operation(summary = "Accept appointment request", description = "Midwife accepts a pending appointment request")
    public ResponseEntity<AppointmentRequestResponseDto> acceptRequest(
        @PathVariable Long midwifeId,
        @PathVariable String requestId,
        Authentication authentication
    ) {
        validateMidwifeAuthentication(authentication, midwifeId);
        return ResponseEntity.ok(appointmentRequestService.acceptRequest(midwifeId, requestId));
    }

    @PatchMapping("/midwife/{midwifeId}/{requestId}/decline")
    @Operation(summary = "Decline appointment request", description = "Midwife declines a pending appointment request")
    public ResponseEntity<AppointmentRequestResponseDto> declineRequest(
        @PathVariable Long midwifeId,
        @PathVariable String requestId,
        Authentication authentication
    ) {
        validateMidwifeAuthentication(authentication, midwifeId);
        return ResponseEntity.ok(appointmentRequestService.declineRequest(midwifeId, requestId));
    }

    private void validateAuthenticatedUser(Authentication authentication, Long midwifeId, Long userId) {
        Long authenticatedUserId = getAuthenticatedUserId(authentication);

        if (!authenticatedUserId.equals(midwifeId) && !authenticatedUserId.equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to access this resource.");
        }
    }

    private void validateMidwifeAuthentication(Authentication authentication, Long midwifeId) {
        Long authenticatedUserId = getAuthenticatedUserId(authentication);

        if (!authenticatedUserId.equals(midwifeId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You don't have permission to access this resource.");
        }
    }

    private Long getAuthenticatedUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required.");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found."));

        return user.getId();
    }
}
