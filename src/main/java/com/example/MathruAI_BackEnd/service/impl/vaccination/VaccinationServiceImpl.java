package com.example.MathruAI_BackEnd.service.impl.vaccination;

import com.example.MathruAI_BackEnd.dto.vaccination.*;
import com.example.MathruAI_BackEnd.entity.Role;
import com.example.MathruAI_BackEnd.entity.User;
import com.example.MathruAI_BackEnd.entity.vaccination.VaccinationCard;
import com.example.MathruAI_BackEnd.entity.vaccination.VaccinationStatus;
import com.example.MathruAI_BackEnd.repository.UserRepository;
import com.example.MathruAI_BackEnd.repository.vaccination.VaccinationCardRepository;
import com.example.MathruAI_BackEnd.service.interservice.vaccination.VaccinationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class VaccinationServiceImpl implements VaccinationService {

    private final VaccinationCardRepository vaccinationRepository;
    private final UserRepository userRepository;

    @Override
    public VaccinationCardResponseDto createGlobalCard(Long midwifeId, VaccinationCardRequestDto request) {
        User midwife = getMidwifeOrThrow(midwifeId);
        validateRequest(request);

        VaccinationCard card = VaccinationCard.builder()
                .midwife(midwife)
                .patient(null)
                .vaccineName(request.getVaccineName().trim())
                .vaccineType(clean(request.getVaccineType()))
                .dose(clean(request.getDose()))
                .dueDate(request.getDueDate())
                .status(resolveStatus(request))
                .midwifeNote(clean(request.getMidwifeNote()))
                .completedDate(request.getCompletedDate())
                .vaccinationInjectionDate(request.getVaccinationInjectionDate())
                .location(clean(request.getLocation()))
                .build();

        return map(vaccinationRepository.save(card));
    }

    @Override
    public VaccinationCardResponseDto createPatientCard(Long midwifeId, Long patientId, VaccinationCardRequestDto request) {
        User midwife = getMidwifeOrThrow(midwifeId);
        User patient = getAssignedPatientOrThrow(midwifeId, patientId);
        validateRequest(request);

        VaccinationCard card = VaccinationCard.builder()
                .midwife(midwife)
                .patient(patient)
                .vaccineName(request.getVaccineName().trim())
                .vaccineType(clean(request.getVaccineType()))
                .dose(clean(request.getDose()))
                .dueDate(request.getDueDate())
                .status(resolveStatus(request))
                .midwifeNote(clean(request.getMidwifeNote()))
                .completedDate(request.getCompletedDate())
                .vaccinationInjectionDate(request.getVaccinationInjectionDate())
                .location(clean(request.getLocation()))
                .build();

        return map(vaccinationRepository.save(card));
    }

    @Override
    public VaccinationCardResponseDto assignGlobalCardToPatient(Long midwifeId, Long patientId, Long cardId) {
        User midwife = getMidwifeOrThrow(midwifeId);
        User patient = getAssignedPatientOrThrow(midwifeId, patientId);
        VaccinationCard template = getOwnedCardOrThrow(midwifeId, cardId);

        if (template.getPatient() != null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only midwife-created vaccination cards can be assigned to a patient."
            );
        }

        boolean alreadyAssigned =
                vaccinationRepository.existsByMidwifeIdAndPatientIdAndVaccineNameAndVaccineTypeAndDoseAndDueDate(
                        midwifeId,
                        patientId,
                        template.getVaccineName(),
                        template.getVaccineType(),
                        template.getDose(),
                        template.getDueDate()
                );

        if (alreadyAssigned) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This vaccination card is already assigned to this patient."
            );
        }

        VaccinationCard assignedCard = VaccinationCard.builder()
                .midwife(midwife)
                .patient(patient)
                .vaccineName(template.getVaccineName())
                .vaccineType(template.getVaccineType())
                .dose(template.getDose())
                .dueDate(template.getDueDate())
                .status(template.getStatus())
                .midwifeNote(template.getMidwifeNote())
                .completedDate(template.getCompletedDate())
                .vaccinationInjectionDate(template.getVaccinationInjectionDate())
                .location(template.getLocation())
                .build();

        return map(vaccinationRepository.save(assignedCard));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VaccinationCardResponseDto> getCardsForMidwife(Long midwifeId) {
        getMidwifeOrThrow(midwifeId);

        return vaccinationRepository.findByMidwifeIdAndPatientIsNullOrderByDueDateAsc(midwifeId)
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VaccinationCardResponseDto> getGlobalCardsForMidwife(Long midwifeId) {
        getMidwifeOrThrow(midwifeId);

        return vaccinationRepository.findByMidwifeIdAndPatientIsNullOrderByDueDateAsc(midwifeId)
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VaccinationCardResponseDto> getCardsForPatient(Long midwifeId, Long patientId) {
        getAssignedPatientOrThrow(midwifeId, patientId);

        return vaccinationRepository.findByMidwifeIdAndPatientIdOrderByDueDateAsc(midwifeId, patientId)
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    public VaccinationCardResponseDto updateCard(Long midwifeId, Long cardId, VaccinationCardRequestDto request) {
        getMidwifeOrThrow(midwifeId);
        VaccinationCard card = getOwnedCardOrThrow(midwifeId, cardId);
        validateRequest(request);

        card.setVaccineName(request.getVaccineName().trim());
        card.setVaccineType(clean(request.getVaccineType()));
        card.setDose(clean(request.getDose()));
        card.setDueDate(request.getDueDate());
        card.setStatus(resolveStatus(request));
        card.setMidwifeNote(clean(request.getMidwifeNote()));
        card.setCompletedDate(request.getCompletedDate());
        card.setVaccinationInjectionDate(request.getVaccinationInjectionDate());
        card.setLocation(clean(request.getLocation()));

        return map(vaccinationRepository.save(card));
    }

    @Override
    public void deleteCard(Long midwifeId, Long cardId) {
        getMidwifeOrThrow(midwifeId);
        VaccinationCard card = getOwnedCardOrThrow(midwifeId, cardId);
        vaccinationRepository.delete(card);
    }

    @Override
    @Transactional(readOnly = true)
    public VaccinationSummaryDto getSummary(Long midwifeId) {
        getMidwifeOrThrow(midwifeId);

        LocalDate today = LocalDate.now();

        return VaccinationSummaryDto.builder()
                .totalCards(vaccinationRepository.countByMidwifeIdAndPatientIsNull(midwifeId))
                .upcomingVaccinations(
                        vaccinationRepository.countByMidwifeIdAndPatientIsNullAndDueDateAfterAndStatus(
                                midwifeId,
                                today,
                                VaccinationStatus.PENDING
                        )
                )
                .todayVaccinations(
                        vaccinationRepository.countByMidwifeIdAndPatientIsNullAndDueDate(
                                midwifeId,
                                today
                        )
                )
                .missedPatients(
                        vaccinationRepository.countDistinctPatientsByMidwifeAndStatus(
                                midwifeId,
                                VaccinationStatus.MISSED
                        )
                )
                .completedPatients(
                        vaccinationRepository.countDistinctPatientsByMidwifeAndStatus(
                                midwifeId,
                                VaccinationStatus.COMPLETED
                        )
                )
                .pendingPatients(
                        vaccinationRepository.countDistinctPatientsByMidwifeAndStatus(
                                midwifeId,
                                VaccinationStatus.PENDING
                        )
                )
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VaccinationCardResponseDto> getUpcoming(Long midwifeId, int days) {
        getMidwifeOrThrow(midwifeId);

        LocalDate today = LocalDate.now();
        int safeDays = Math.max(1, Math.min(days, 180));

        return vaccinationRepository.findUpcoming(midwifeId, today, today.plusDays(safeDays))
                .stream()
                .map(this::map)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VaccineEligibilityDto> getEligibility(Long midwifeId) {
        getMidwifeOrThrow(midwifeId);

        return vaccinationRepository.countEligiblePatientsByVaccine(midwifeId)
                .stream()
                .map(row -> VaccineEligibilityDto.builder()
                        .vaccineName((String) row[0])
                        .vaccineType((String) row[1])
                        .dose((String) row[2])
                        .eligiblePatients(((Number) row[3]).longValue())
                        .build())
                .toList();
    }

    private User getMidwifeOrThrow(Long midwifeId) {
        User midwife = userRepository.findById(midwifeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Midwife not found with id: " + midwifeId
                ));

        if (midwife.getRoles() == null || !midwife.getRoles().contains(Role.MIDWIFE)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not a midwife.");
        }

        return midwife;
    }

    private User getAssignedPatientOrThrow(Long midwifeId, Long patientId) {
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Patient not found with id: " + patientId
                ));

        if (patient.getAssignedMidwife() == null ||
                !Objects.equals(patient.getAssignedMidwife().getId(), midwifeId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "This patient is not assigned to the given midwife."
            );
        }

        return patient;
    }

    private VaccinationCard getOwnedCardOrThrow(Long midwifeId, Long cardId) {
        VaccinationCard card = vaccinationRepository.findById(cardId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Vaccination card not found with id: " + cardId
                ));

        if (!Objects.equals(card.getMidwife().getId(), midwifeId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You cannot modify another midwife's vaccination card."
            );
        }

        return card;
    }

    private void validateRequest(VaccinationCardRequestDto request) {
        if (request.getVaccineName() == null || request.getVaccineName().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Vaccine name is required."
            );
        }

        if (request.getDueDate() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Due date is required."
            );
        }

        VaccinationStatus status = resolveStatus(request);

        if (status == VaccinationStatus.COMPLETED && request.getCompletedDate() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Completed date is required when status is COMPLETED."
            );
        }

        if (status != VaccinationStatus.COMPLETED && request.getCompletedDate() != null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Completed date should only be provided when status is COMPLETED."
            );
        }
    }

    private VaccinationStatus resolveStatus(VaccinationCardRequestDto request) {
        return request.getStatus() == null ? VaccinationStatus.PENDING : request.getStatus();
    }

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private VaccinationCardResponseDto map(VaccinationCard card) {
        User patient = card.getPatient();

        String patientName = null;

        if (patient != null) {
            patientName = ((patient.getFirstName() == null ? "" : patient.getFirstName()) + " " +
                    (patient.getLastName() == null ? "" : patient.getLastName())).trim();

            if (patientName.isBlank()) {
                patientName = patient.getEmail();
            }
        }

        return VaccinationCardResponseDto.builder()
                .id(card.getId())
                .midwifeId(card.getMidwife().getId())
                .patientId(patient != null ? patient.getId() : null)
                .patientName(patientName)
                .patientEmail(patient != null ? patient.getEmail() : null)
                .vaccineName(card.getVaccineName())
                .vaccineType(card.getVaccineType())
                .dose(card.getDose())
                .dueDate(card.getDueDate())
                .status(card.getStatus())
                .midwifeNote(card.getMidwifeNote())
                .completedDate(card.getCompletedDate())
                .vaccinationInjectionDate(card.getVaccinationInjectionDate())
                .location(card.getLocation())
                .createdAt(card.getCreatedAt())
                .updatedAt(card.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<VaccinationCardResponseDto> getCardsForPatientSide(Long patientId) {
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Patient not found with id: " + patientId
                ));

        if (patient.getAssignedMidwife() == null) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "No midwife is assigned to this patient."
            );
        }

        return vaccinationRepository.findByPatientIdOrderByDueDateAsc(patientId)
                .stream()
                .map(this::map)
                .toList();
    }
}