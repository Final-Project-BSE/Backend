package com.example.MathruAI_BackEnd.service.interservice.vaccination;

import com.example.MathruAI_BackEnd.dto.vaccination.*;

import java.util.List;

public interface VaccinationService {

    VaccinationCardResponseDto createGlobalCard(Long midwifeId, VaccinationCardRequestDto request);

    VaccinationCardResponseDto createPatientCard(Long midwifeId, Long patientId, VaccinationCardRequestDto request);

    VaccinationCardResponseDto assignGlobalCardToPatient(Long midwifeId, Long patientId, Long cardId);

    List<VaccinationCardResponseDto> getCardsForMidwife(Long midwifeId);

    List<VaccinationCardResponseDto> getGlobalCardsForMidwife(Long midwifeId);

    List<VaccinationCardResponseDto> getCardsForPatient(Long midwifeId, Long patientId);

    VaccinationCardResponseDto updateCard(Long midwifeId, Long cardId, VaccinationCardRequestDto request);

    void deleteCard(Long midwifeId, Long cardId);

    VaccinationSummaryDto getSummary(Long midwifeId);

    List<VaccinationCardResponseDto> getUpcoming(Long midwifeId, int days);

    List<VaccineEligibilityDto> getEligibility(Long midwifeId);

    List<VaccinationCardResponseDto> getCardsForPatientSide(Long patientId);
}