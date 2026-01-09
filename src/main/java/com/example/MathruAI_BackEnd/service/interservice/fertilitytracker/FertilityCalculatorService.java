//package com.example.MathruAI_BackEnd.service.interservice.fertilitytracker;
//
//import com.example.MathruAI_BackEnd.dto.reproductiveDto.fertilitytracker.FertilityRequestDto;
//import com.example.MathruAI_BackEnd.dto.reproductiveDto.fertilitytracker.FertilityResponseDto;
//import com.example.MathruAI_BackEnd.entity.reproductive.fertilitytracker.CycleData;
//import com.example.MathruAI_BackEnd.exception.fertilitytracker.InvalidInputException;
//import com.example.MathruAI_BackEnd.repository.reproductive.fertilitytracker.CycleDataRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//@Service
//@RequiredArgsConstructor
//public class FertilityCalculatorService {
//
//    private final CycleDataRepository cycleDataRepository;
//
//    public FertilityResponseDto calculateAndSave(FertilityRequestDto request) {
//
//        if (request.getAverageCycleLength() < 21 || request.getAverageCycleLength() > 35) {
//            throw new InvalidInputException("Average cycle length must be between 21 and 35 days");
//        }
//
//        LocalDate lastPeriod = request.getLastPeriodDate();
//        int cycleLength = request.getAverageCycleLength();
//
//        LocalDate ovulationDate = lastPeriod.plusDays(cycleLength - 14);
//        LocalDate fertileStart = ovulationDate.minusDays(5);
//        LocalDate fertileEnd = ovulationDate.plusDays(1);
//        LocalDate nextPeriodDate = lastPeriod.plusDays(cycleLength);
//        LocalDate pregnancyTestDay = nextPeriodDate.plusDays(1);
//
//        // 🔹 SAVE INPUT DATA PER USER
//        CycleData cycleData = CycleData.builder()
//                .userId(request.getUserId())
//                .lastPeriodDate(lastPeriod)
//                .averageCycleLength(cycleLength)
//                .build();
//
//
//        cycleDataRepository.save(cycleData);
//
//        // 🔹 RETURN RESPONSE
//        return new FertilityResponseDto(
//                fertileStart,
//                fertileEnd,
//                ovulationDate,
//                nextPeriodDate,
//                pregnancyTestDay
//        );
//    }
//}






package com.example.MathruAI_BackEnd.service.interservice.fertilitytracker;

import com.example.MathruAI_BackEnd.dto.reproductiveDto.fertilitytracker.FertilityRequestDto;
import com.example.MathruAI_BackEnd.dto.reproductiveDto.fertilitytracker.FertilityResponseDto;
import com.example.MathruAI_BackEnd.entity.reproductive.fertilitytracker.CycleData;
import com.example.MathruAI_BackEnd.exception.fertilitytracker.InvalidInputException;
import com.example.MathruAI_BackEnd.repository.reproductive.fertilitytracker.CycleDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class FertilityCalculatorService {

    private final CycleDataRepository cycleDataRepository;

    public FertilityResponseDto calculateAndSave(FertilityRequestDto request) {

        if (request.getAverageCycleLength() < 21 ||
                request.getAverageCycleLength() > 35) {
            throw new InvalidInputException(
                    "Average cycle length must be between 21 and 35 days"
            );
        }

        LocalDate lastPeriod = request.getLastPeriodDate();
        int cycleLength = request.getAverageCycleLength();

        LocalDate ovulationDate = lastPeriod.plusDays(cycleLength - 14);
        LocalDate fertileStart = ovulationDate.minusDays(5);
        LocalDate fertileEnd = ovulationDate.plusDays(1);
        LocalDate nextPeriodDate = lastPeriod.plusDays(cycleLength);
        LocalDate pregnancyTestDay = nextPeriodDate.plusDays(1);

        // 🔹 Save request + response
        CycleData cycleData = CycleData.builder()
                .userId(request.getUserId())
                .lastPeriodDate(lastPeriod)
                .averageCycleLength(cycleLength)
                .fertileWindowStart(fertileStart)
                .fertileWindowEnd(fertileEnd)
                .ovulationDate(ovulationDate)
                .nextPeriodDate(nextPeriodDate)
                .pregnancyTestDay(pregnancyTestDay)
                .build();

        cycleDataRepository.save(cycleData);

        return new FertilityResponseDto(
                fertileStart,
                fertileEnd,
                ovulationDate,
                nextPeriodDate,
                pregnancyTestDay
        );
    }

    public CycleData getLatestByUserId(Long userId) {
        return cycleDataRepository.findTopByUserIdOrderByIdDesc(userId)
                .orElseThrow(() ->
                        new RuntimeException("No fertility data found for user"));
    }
}
