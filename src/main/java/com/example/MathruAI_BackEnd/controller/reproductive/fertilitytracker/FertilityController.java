package com.example.MathruAI_BackEnd.controller.reproductive.fertilitytracker;

import com.example.MathruAI_BackEnd.dto.reproductiveDto.fertilitytracker.FertilityRequestDto;
import com.example.MathruAI_BackEnd.dto.reproductiveDto.fertilitytracker.FertilityResponseDto;
import com.example.MathruAI_BackEnd.entity.reproductive.fertilitytracker.CycleData;
import com.example.MathruAI_BackEnd.service.interservice.fertilitytracker.FertilityCalculatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fertility")
public class FertilityController {

    private static final Logger log = LoggerFactory.getLogger(FertilityController.class);
    private final FertilityCalculatorService service;

    public FertilityController(FertilityCalculatorService service) {
        this.service = service;
        log.info("✅ FertilityController initialized");
    }

    @PostMapping("/calculate")
    public ResponseEntity<FertilityResponseDto> calculateFertility(
            Authentication authentication,
            @RequestBody FertilityRequestDto request
    ) {
        String userSub = authentication.getName(); // email
        FertilityResponseDto response = service.calculateAndSave(userSub, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/latest")
    public ResponseEntity<FertilityResponseDto> getLatest(Authentication authentication) {
        String userSub = authentication.getName();

        CycleData data = service.getLatestForUser(userSub);
        return ResponseEntity.ok(new FertilityResponseDto(
                data.getFertileWindowStart(),
                data.getFertileWindowEnd(),
                data.getOvulationDate(),
                data.getNextPeriodDate(),
                data.getPregnancyTestDay()
        ));
    }
}
