package com.example.MathruAI_BackEnd.service.interservice.fertilitytracker;

import com.example.MathruAI_BackEnd.dto.reproductiveDto.fertilitytracker.FertilityRequestDto;
import com.example.MathruAI_BackEnd.dto.reproductiveDto.fertilitytracker.FertilityResponseDto;
import com.example.MathruAI_BackEnd.entity.Role;
import com.example.MathruAI_BackEnd.entity.User;
import com.example.MathruAI_BackEnd.entity.reproductive.fertilitytracker.CycleData;
import com.example.MathruAI_BackEnd.exception.fertilitytracker.InvalidInputException;
import com.example.MathruAI_BackEnd.repository.UserRepository;
import com.example.MathruAI_BackEnd.repository.reproductive.fertilitytracker.CycleDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class FertilityCalculatorService {

    private final CycleDataRepository cycleDataRepository;
    private final UserRepository userRepository;

    public FertilityResponseDto calculateAndSave(String userSub, FertilityRequestDto request) {

        LocalDate lastPeriod;

        if (request.getLastPeriodDate() != null) {
            lastPeriod = request.getLastPeriodDate();
        } else {
            CycleData lastCycle = cycleDataRepository
                    .findTopByUserSubOrderByIdDesc(userSub)
                    .orElseThrow(() ->
                            new InvalidInputException("Last period date required for first cycle"));

            lastPeriod = lastCycle.getLastPeriodDate();
            int cycleLength = lastCycle.getAverageCycleLength();

            LocalDate predictedNext = lastCycle.getNextPeriodDate();
            LocalDate today = LocalDate.now();

            while (today.isAfter(predictedNext)) {
                lastPeriod = predictedNext;
                predictedNext = predictedNext.plusDays(cycleLength);
            }
        }

        if (request.getAverageCycleLength() < 21 || request.getAverageCycleLength() > 35) {
            throw new InvalidInputException("Average cycle length must be between 21 and 35 days");
        }

        User user = userRepository.findByEmail(userSub)
                .orElseThrow(() -> new RuntimeException("User not found for email: " + userSub));

        int cycleLength = request.getAverageCycleLength();

        LocalDate ovulationDate = lastPeriod.plusDays(cycleLength - 14);

        LocalDate fertileStart = ovulationDate.minusDays(6);
        LocalDate fertileEnd = ovulationDate;

        LocalDate nextPeriodDate = lastPeriod.plusDays(cycleLength);
        LocalDate pregnancyTestDay = nextPeriodDate.plusDays(1);

        LocalDate safeStart1 = lastPeriod;
        LocalDate safeEnd1 = fertileStart.minusDays(1);
        if (safeEnd1.isBefore(safeStart1)) safeEnd1 = safeStart1;

        LocalDate safeStart2 = fertileEnd.plusDays(1);
        LocalDate safeEnd2 = nextPeriodDate.minusDays(1);
        if (safeEnd2.isBefore(safeStart2)) safeEnd2 = safeStart2;

        CycleData cycleData = new CycleData();
        cycleData.setUser(user);
        cycleData.setUserSub(userSub);
        cycleData.setLastPeriodDate(lastPeriod);
        cycleData.setAverageCycleLength(cycleLength);
        cycleData.setFertileWindowStart(fertileStart);
        cycleData.setFertileWindowEnd(fertileEnd);
        cycleData.setOvulationDate(ovulationDate);
        cycleData.setNextPeriodDate(nextPeriodDate);
        cycleData.setPregnancyTestDay(pregnancyTestDay);

        cycleData.setSafeStart1(safeStart1);
        cycleData.setSafeEnd1(safeEnd1);
        cycleData.setSafeStart2(safeStart2);
        cycleData.setSafeEnd2(safeEnd2);

        cycleDataRepository.save(cycleData);

        return new FertilityResponseDto(
                fertileStart,
                fertileEnd,
                ovulationDate,
                nextPeriodDate,
                pregnancyTestDay,
                safeStart1,
                safeEnd1,
                safeStart2,
                safeEnd2
        );
    }

    public CycleData getLatestForUser(String userSub) {
        return cycleDataRepository.findTopByUserSubOrderByIdDesc(userSub)
                .orElseThrow(() -> new RuntimeException("No fertility data found for user"));
    }

    public FertilityResponseDto getLatestForAssignedPatient(Long midwifeId, Long patientId) {
        User midwife = userRepository.findById(midwifeId)
                .orElseThrow(() -> new RuntimeException("Midwife not found with id: " + midwifeId));

        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + patientId));

        if (midwife.getRoles() == null || !midwife.getRoles().contains(Role.MIDWIFE)) {
            throw new RuntimeException("User is not a midwife.");
        }

        if (patient.getAssignedMidwife() == null ||
                !patient.getAssignedMidwife().getId().equals(midwifeId)) {
            throw new RuntimeException("Patient is not assigned to this midwife.");
        }

        CycleData data = cycleDataRepository.findTopByUserSubOrderByIdDesc(patient.getEmail())
                .orElseThrow(() -> new RuntimeException("No fertility data found for assigned patient."));

        return new FertilityResponseDto(
                data.getFertileWindowStart(),
                data.getFertileWindowEnd(),
                data.getOvulationDate(),
                data.getNextPeriodDate(),
                data.getPregnancyTestDay(),
                data.getSafeStart1(),
                data.getSafeEnd1(),
                data.getSafeStart2(),
                data.getSafeEnd2()
        );
    }
}