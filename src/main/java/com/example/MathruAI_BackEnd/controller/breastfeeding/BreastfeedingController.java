package com.example.MathruAI_BackEnd.controller.breastfeeding;

import com.example.MathruAI_BackEnd.dto.breastfeeding.BreastfeedingIssueRequestDto;
import com.example.MathruAI_BackEnd.dto.breastfeeding.BreastfeedingIssueResponseDto;
import com.example.MathruAI_BackEnd.dto.breastfeeding.BreastfeedingSessionRequestDto;
import com.example.MathruAI_BackEnd.dto.breastfeeding.BreastfeedingSessionResponseDto;
import com.example.MathruAI_BackEnd.dto.breastfeeding.BreastfeedingTipRequestDto;
import com.example.MathruAI_BackEnd.dto.breastfeeding.BreastfeedingTipResponseDto;
import com.example.MathruAI_BackEnd.service.interservice.breastfeeding.BreastfeedingServiceInter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/breastfeeding")
@RequiredArgsConstructor
@Tag(name = "Breastfeeding Support", description = "Management of breastfeeding sessions, issues and tips")
public class BreastfeedingController {

    private final BreastfeedingServiceInter breastfeedingService;

    private String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    // SESSION
    @GetMapping("/sessions")
    @Operation(summary = "Get all sessions", description = "Fetch all breastfeeding sessions for the logged-in user")
    public ResponseEntity<List<BreastfeedingSessionResponseDto>> getAllSessions() {
        return ResponseEntity.ok(breastfeedingService.getAllSessions(getCurrentUserEmail()));
    }

    @PostMapping("/sessions")
    @Operation(summary = "Create session", description = "Log a new breastfeeding session")
    public ResponseEntity<BreastfeedingSessionResponseDto> createSession(
            @RequestBody BreastfeedingSessionRequestDto request) {
        return ResponseEntity.ok(breastfeedingService.createSession(getCurrentUserEmail(), request));
    }

    @PutMapping("/sessions/{sessionId}")
    @Operation(summary = "Update session", description = "Update an existing breastfeeding session")
    public ResponseEntity<BreastfeedingSessionResponseDto> updateSession(
            @PathVariable UUID sessionId,
            @RequestBody BreastfeedingSessionRequestDto request) {
        return ResponseEntity.ok(breastfeedingService.updateSession(getCurrentUserEmail(), sessionId, request));
    }

    @DeleteMapping("/sessions/{sessionId}")
    @Operation(summary = "Delete session", description = "Delete a breastfeeding session")
    public ResponseEntity<Void> deleteSession(@PathVariable UUID sessionId) {
        breastfeedingService.deleteSession(getCurrentUserEmail(), sessionId);
        return ResponseEntity.noContent().build();
    }

    //ISSUE

    @GetMapping("/issues")
    @Operation(summary = "Get all issues", description = "Fetch all breastfeeding issues for the logged-in user")
    public ResponseEntity<List<BreastfeedingIssueResponseDto>> getAllIssues() {
        return ResponseEntity.ok(breastfeedingService.getAllIssues(getCurrentUserEmail()));
    }

    @GetMapping("/issues/unresolved")
    @Operation(summary = "Get unresolved issues", description = "Fetch all unresolved breastfeeding issues")
    public ResponseEntity<List<BreastfeedingIssueResponseDto>> getUnresolvedIssues() {
        return ResponseEntity.ok(breastfeedingService.getUnresolvedIssues(getCurrentUserEmail()));
    }

    @PostMapping("/issues")
    @Operation(summary = "Create issue", description = "Report a new breastfeeding issue")
    public ResponseEntity<BreastfeedingIssueResponseDto> createIssue(
            @RequestBody BreastfeedingIssueRequestDto request) {
        return ResponseEntity.ok(breastfeedingService.createIssue(getCurrentUserEmail(), request));
    }

    @PutMapping("/issues/{issueId}")
    @Operation(summary = "Update issue", description = "Update an existing breastfeeding issue")
    public ResponseEntity<BreastfeedingIssueResponseDto> updateIssue(
            @PathVariable UUID issueId,
            @RequestBody BreastfeedingIssueRequestDto request) {
        return ResponseEntity.ok(breastfeedingService.updateIssue(getCurrentUserEmail(), issueId, request));
    }

    @DeleteMapping("/issues/{issueId}")
    @Operation(summary = "Delete issue", description = "Delete a breastfeeding issue")
    public ResponseEntity<Void> deleteIssue(@PathVariable UUID issueId) {
        breastfeedingService.deleteIssue(getCurrentUserEmail(), issueId);
        return ResponseEntity.noContent().build();
    }

    //TIP

    @GetMapping("/tips")
    @Operation(summary = "Get all active tips", description = "Fetch all active breastfeeding tips")
    public ResponseEntity<List<BreastfeedingTipResponseDto>> getAllActiveTips() {
        return ResponseEntity.ok(breastfeedingService.getAllActiveTips());
    }

    @PostMapping("/tips")
    @Operation(summary = "Create tip", description = "Midwife creates a new breastfeeding tip")
    public ResponseEntity<BreastfeedingTipResponseDto> createTip(
            @RequestBody BreastfeedingTipRequestDto request) {
        return ResponseEntity.ok(breastfeedingService.createTip(getCurrentUserEmail(), request));
    }

    @PutMapping("/tips/{tipId}")
    @Operation(summary = "Update tip", description = "Update an existing breastfeeding tip")
    public ResponseEntity<BreastfeedingTipResponseDto> updateTip(
            @PathVariable UUID tipId,
            @RequestBody BreastfeedingTipRequestDto request) {
        return ResponseEntity.ok(breastfeedingService.updateTip(getCurrentUserEmail(), tipId, request));
    }

    @DeleteMapping("/tips/{tipId}")
    @Operation(summary = "Delete tip", description = "Delete a breastfeeding tip")
    public ResponseEntity<Void> deleteTip(@PathVariable UUID tipId) {
        breastfeedingService.deleteTip(getCurrentUserEmail(), tipId);
        return ResponseEntity.noContent().build();
    }

    //MIDWIFE

    @GetMapping("/midwife/{midwifeId}/patient/{patientId}/sessions")
    @Operation(summary = "Get patient sessions for midwife", description = "Fetch breastfeeding sessions for an assigned patient")
    public ResponseEntity<List<BreastfeedingSessionResponseDto>> getPatientSessionsForMidwife(
            @PathVariable Long midwifeId,
            @PathVariable Long patientId) {
        return ResponseEntity.ok(
                breastfeedingService.getPatientSessionsForMidwife(midwifeId, patientId)
        );
    }

    @GetMapping("/midwife/{midwifeId}/patient/{patientId}/issues")
    @Operation(summary = "Get patient issues for midwife", description = "Fetch breastfeeding issues for an assigned patient")
    public ResponseEntity<List<BreastfeedingIssueResponseDto>> getPatientIssuesForMidwife(
            @PathVariable Long midwifeId,
            @PathVariable Long patientId) {
        return ResponseEntity.ok(
                breastfeedingService.getPatientIssuesForMidwife(midwifeId, patientId)
        );
    }

    @PutMapping("/midwife/{midwifeId}/patient/{patientId}/issues/{issueId}")
    @Operation(summary = "Update patient issue for midwife", description = "Midwife updates note or resolves a patient issue")
    public ResponseEntity<BreastfeedingIssueResponseDto> updatePatientIssueForMidwife(
            @PathVariable Long midwifeId,
            @PathVariable Long patientId,
            @PathVariable UUID issueId,
            @RequestBody BreastfeedingIssueRequestDto request) {
        return ResponseEntity.ok(
                breastfeedingService.updatePatientIssueForMidwife(midwifeId, patientId, issueId, request)
        );
    }
}