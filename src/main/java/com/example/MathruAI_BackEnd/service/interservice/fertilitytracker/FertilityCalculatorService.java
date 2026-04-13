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
    private final UserRepository userRepository; //

    public FertilityResponseDto calculateAndSave(String userSub, FertilityRequestDto request) {

        LocalDate lastPeriod;

        if (request.getLastPeriodDate() != null) {

            // user entered new period
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

            // auto progress cycles if today passed predicted period
            while (today.isAfter(predictedNext)) {
                lastPeriod = predictedNext;
                predictedNext = predictedNext.plusDays(cycleLength);
            }
        }



        if (request.getAverageCycleLength() < 21 || request.getAverageCycleLength() > 35) {
            throw new InvalidInputException("Average cycle length must be between 21 and 35 days");
        }

        //  Find actual User (required because CycleData.user is NOT NULL)
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
        cycleData.setUser(user);         //  IMPORTANT: fills user_id
        cycleData.setUserSub(userSub);   // (optional, but your table has NOT NULL so keep)
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
}
