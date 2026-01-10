package com.example.MathruAI_BackEnd.service.interservice.fertilitytracker;

import com.example.MathruAI_BackEnd.dto.reproductiveDto.fertilitytracker.FertilityRequestDto;
import com.example.MathruAI_BackEnd.dto.reproductiveDto.fertilitytracker.FertilityResponseDto;
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
    private final UserRepository userRepository; // ✅ added

    public FertilityResponseDto calculateAndSave(String userSub, FertilityRequestDto request) {

        if (request.getLastPeriodDate() == null) {
            throw new InvalidInputException("Last period date is required");
        }

        if (request.getAverageCycleLength() < 21 || request.getAverageCycleLength() > 35) {
            throw new InvalidInputException("Average cycle length must be between 21 and 35 days");
        }

        // ✅ Find actual User (required because CycleData.user is NOT NULL)
        User user = userRepository.findByEmail(userSub)
                .orElseThrow(() -> new RuntimeException("User not found for email: " + userSub));

        LocalDate lastPeriod = request.getLastPeriodDate();
        int cycleLength = request.getAverageCycleLength();

        LocalDate ovulationDate = lastPeriod.plusDays(cycleLength - 14);
        LocalDate fertileStart = ovulationDate.minusDays(5);
        LocalDate fertileEnd = ovulationDate.plusDays(1);
        LocalDate nextPeriodDate = lastPeriod.plusDays(cycleLength);
        LocalDate pregnancyTestDay = nextPeriodDate.plusDays(1);

        CycleData cycleData = new CycleData();
        cycleData.setUser(user);         // ✅ IMPORTANT: fills user_id
        cycleData.setUserSub(userSub);   // (optional, but your table has NOT NULL so keep)
        cycleData.setLastPeriodDate(lastPeriod);
        cycleData.setAverageCycleLength(cycleLength);
        cycleData.setFertileWindowStart(fertileStart);
        cycleData.setFertileWindowEnd(fertileEnd);
        cycleData.setOvulationDate(ovulationDate);
        cycleData.setNextPeriodDate(nextPeriodDate);
        cycleData.setPregnancyTestDay(pregnancyTestDay);

        cycleDataRepository.save(cycleData);

        return new FertilityResponseDto(
                fertileStart,
                fertileEnd,
                ovulationDate,
                nextPeriodDate,
                pregnancyTestDay
        );
    }

    public CycleData getLatestForUser(String userSub) {
        return cycleDataRepository.findTopByUserSubOrderByIdDesc(userSub)
                .orElseThrow(() -> new RuntimeException("No fertility data found for user"));
    }
}
