package com.example.MathruAI_BackEnd.controller.vaccination;

import com.example.MathruAI_BackEnd.dto.vaccination.*;
import com.example.MathruAI_BackEnd.service.interservice.vaccination.VaccinationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vaccinations")
@RequiredArgsConstructor
public class VaccinationController {

    private final VaccinationService vaccinationService;

    @PostMapping("/midwife/{midwifeId}")
    public ResponseEntity<VaccinationCardResponseDto> createGlobalCard(
            @PathVariable Long midwifeId,
            @Valid @RequestBody VaccinationCardRequestDto request
    ) {
        return ResponseEntity.ok(vaccinationService.createGlobalCard(midwifeId, request));
    }

    @PostMapping("/midwife/{midwifeId}/patient/{patientId}")
    public ResponseEntity<VaccinationCardResponseDto> createPatientCard(
            @PathVariable Long midwifeId,
            @PathVariable Long patientId,
            @Valid @RequestBody VaccinationCardRequestDto request
    ) {
        return ResponseEntity.ok(vaccinationService.createPatientCard(midwifeId, patientId, request));
    }

    @PostMapping("/midwife/{midwifeId}/patient/{patientId}/assign/{cardId}")
    public ResponseEntity<VaccinationCardResponseDto> assignGlobalCardToPatient(
            @PathVariable Long midwifeId,
            @PathVariable Long patientId,
            @PathVariable Long cardId
    ) {
        return ResponseEntity.ok(
                vaccinationService.assignGlobalCardToPatient(midwifeId, patientId, cardId)
        );
    }

    @GetMapping("/midwife/{midwifeId}")
    public ResponseEntity<List<VaccinationCardResponseDto>> getCardsForMidwife(@PathVariable Long midwifeId) {
        return ResponseEntity.ok(vaccinationService.getCardsForMidwife(midwifeId));
    }

    @GetMapping("/midwife/{midwifeId}/templates")
    public ResponseEntity<List<VaccinationCardResponseDto>> getGlobalCardsForMidwife(@PathVariable Long midwifeId) {
        return ResponseEntity.ok(vaccinationService.getGlobalCardsForMidwife(midwifeId));
    }

    @GetMapping("/midwife/{midwifeId}/patient/{patientId}")
    public ResponseEntity<List<VaccinationCardResponseDto>> getCardsForPatient(
            @PathVariable Long midwifeId,
            @PathVariable Long patientId
    ) {
        return ResponseEntity.ok(vaccinationService.getCardsForPatient(midwifeId, patientId));
    }

    @PutMapping("/midwife/{midwifeId}/cards/{cardId}")
    public ResponseEntity<VaccinationCardResponseDto> updateCard(
            @PathVariable Long midwifeId,
            @PathVariable Long cardId,
            @Valid @RequestBody VaccinationCardRequestDto request
    ) {
        return ResponseEntity.ok(vaccinationService.updateCard(midwifeId, cardId, request));
    }

    @DeleteMapping("/midwife/{midwifeId}/cards/{cardId}")
    public ResponseEntity<String> deleteCard(
            @PathVariable Long midwifeId,
            @PathVariable Long cardId
    ) {
        vaccinationService.deleteCard(midwifeId, cardId);
        return ResponseEntity.ok("Vaccination card deleted successfully.");
    }

    @GetMapping("/midwife/{midwifeId}/summary")
    public ResponseEntity<VaccinationSummaryDto> getSummary(@PathVariable Long midwifeId) {
        return ResponseEntity.ok(vaccinationService.getSummary(midwifeId));
    }

    @GetMapping("/midwife/{midwifeId}/upcoming")
    public ResponseEntity<List<VaccinationCardResponseDto>> getUpcoming(
            @PathVariable Long midwifeId,
            @RequestParam(defaultValue = "30") int days
    ) {
        return ResponseEntity.ok(vaccinationService.getUpcoming(midwifeId, days));
    }

    @GetMapping("/midwife/{midwifeId}/eligibility")
    public ResponseEntity<List<VaccineEligibilityDto>> getEligibility(@PathVariable Long midwifeId) {
        return ResponseEntity.ok(vaccinationService.getEligibility(midwifeId));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<VaccinationCardResponseDto>> getCardsForPatientSide(
            @PathVariable Long patientId
    ) {
        return ResponseEntity.ok(vaccinationService.getCardsForPatientSide(patientId));
    }
}