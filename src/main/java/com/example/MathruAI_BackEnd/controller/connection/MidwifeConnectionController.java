package com.example.MathruAI_BackEnd.controller.connection;

import com.example.MathruAI_BackEnd.dto.connection.AreaMapSearchRequestDto;
import com.example.MathruAI_BackEnd.dto.connection.AreaSearchRequestDto;
import com.example.MathruAI_BackEnd.dto.connection.AssignedUserProfileUpdateRequestDto;
import com.example.MathruAI_BackEnd.dto.connection.ConnectionRequestResponseDto;
import com.example.MathruAI_BackEnd.dto.connection.SendConnectionRequestDto;
import com.example.MathruAI_BackEnd.dto.userDto.UserResponseDto;
import com.example.MathruAI_BackEnd.service.interservice.connection.MidwifeConnectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/connections")
@RequiredArgsConstructor
public class MidwifeConnectionController {

    private final MidwifeConnectionService connectionService;

    @PostMapping("/send/{senderId}")
    public ResponseEntity<List<ConnectionRequestResponseDto>> sendRequest(
            @PathVariable Long senderId,
            @RequestBody SendConnectionRequestDto request
    ) {
        return ResponseEntity.ok(connectionService.sendRequest(senderId, request));
    }

    @PatchMapping("/{requestId}/approve/{approverUserId}")
    public ResponseEntity<ConnectionRequestResponseDto> approveRequest(
            @PathVariable Long requestId,
            @PathVariable Long approverUserId
    ) {
        return ResponseEntity.ok(connectionService.approveRequest(requestId, approverUserId));
    }

    @PatchMapping("/{requestId}/reject/{approverUserId}")
    public ResponseEntity<ConnectionRequestResponseDto> rejectRequest(
            @PathVariable Long requestId,
            @PathVariable Long approverUserId
    ) {
        return ResponseEntity.ok(connectionService.rejectRequest(requestId, approverUserId));
    }

    @GetMapping("/sent/{userId}")
    public ResponseEntity<List<ConnectionRequestResponseDto>> getSentRequests(@PathVariable Long userId) {
        return ResponseEntity.ok(connectionService.getSentRequests(userId));
    }

    @GetMapping("/received/{userId}")
    public ResponseEntity<List<ConnectionRequestResponseDto>> getReceivedRequests(@PathVariable Long userId) {
        return ResponseEntity.ok(connectionService.getReceivedRequests(userId));
    }

    @GetMapping("/midwife/{midwifeId}/assigned-users")
    public ResponseEntity<List<UserResponseDto>> getAssignedUsersForMidwife(@PathVariable Long midwifeId) {
        return ResponseEntity.ok(connectionService.getAssignedUsersForMidwife(midwifeId));
    }

    @GetMapping("/mother/{motherUserId}/assigned-midwife")
    public ResponseEntity<UserResponseDto> getAssignedMidwifeForMother(@PathVariable Long motherUserId) {
        return ResponseEntity.ok(connectionService.getAssignedMidwifeForMother(motherUserId));
    }

    @PutMapping("/midwife/{midwifeId}/assigned-users/{motherUserId}")
    public ResponseEntity<UserResponseDto> updateAssignedMotherProfile(
            @PathVariable Long midwifeId,
            @PathVariable Long motherUserId,
            @RequestBody AssignedUserProfileUpdateRequestDto request
    ) {
        return ResponseEntity.ok(connectionService.updateAssignedMotherProfile(midwifeId, motherUserId, request));
    }

    @PostMapping("/search/{requesterId}")
    public ResponseEntity<List<UserResponseDto>> searchUsersByDistrictAndMohArea(
            @PathVariable Long requesterId,
            @RequestBody AreaSearchRequestDto request
    ) {
        return ResponseEntity.ok(
                connectionService.searchUsersByDistrictAndMohArea(requesterId, request)
        );
    }

    /**
     * Same role logic as normal search, but only returns users
     * who have latitude + longitude for map display.
     */
    @PostMapping("/map-search/{requesterId}")
    public ResponseEntity<List<UserResponseDto>> searchMappableUsersByDistrictAndMohArea(
            @PathVariable Long requesterId,
            @RequestBody AreaMapSearchRequestDto request
    ) {
        return ResponseEntity.ok(
                connectionService.searchMappableUsersByDistrictAndMohArea(requesterId, request)
        );
    }
}