//package com.example.MathruAI_BackEnd.controller.reproductive.fertilitytracker;
//
//import com.example.MathruAI_BackEnd.dto.reproductiveDto.fertilitytracker.FertilityRequestDto;
//import com.example.MathruAI_BackEnd.dto.reproductiveDto.fertilitytracker.FertilityResponseDto;
//import com.example.MathruAI_BackEnd.service.interservice.fertilitytracker.FertilityCalculatorService;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/fertility")
//@CrossOrigin
//public class FertilityController {
//
//    private final FertilityCalculatorService service;
//
//    public FertilityController(FertilityCalculatorService service) {
//        this.service = service;
//    }
//
//
//
//    @PostMapping("/calculate")
//    public FertilityResponseDto calculateFertility(@RequestBody FertilityRequestDto request) {
//        return service.calculateAndSave(request);
//    }
//
//}
package com.example.MathruAI_BackEnd.controller.reproductive.fertilitytracker;

import com.example.MathruAI_BackEnd.dto.reproductiveDto.fertilitytracker.FertilityRequestDto;
import com.example.MathruAI_BackEnd.dto.reproductiveDto.fertilitytracker.FertilityResponseDto;
import com.example.MathruAI_BackEnd.entity.reproductive.fertilitytracker.CycleData;
import com.example.MathruAI_BackEnd.service.interservice.fertilitytracker.FertilityCalculatorService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fertility")
@CrossOrigin
public class FertilityController {

    private final FertilityCalculatorService service;

    public FertilityController(FertilityCalculatorService service) {
        this.service = service;
    }

    // 🔹 Calculate + Save request & response
    @PostMapping("/calculate")
    public FertilityResponseDto calculateFertility(
            @RequestBody FertilityRequestDto request) {
        return service.calculateAndSave(request);
    }

    // 🔹 Get latest saved fertility data for a user
    @GetMapping("/latest/{userId}")
    public FertilityResponseDto getLatest(@PathVariable Long userId) {
        CycleData data = service.getLatestByUserId(userId);

        return new FertilityResponseDto(
                data.getFertileWindowStart(),
                data.getFertileWindowEnd(),
                data.getOvulationDate(),
                data.getNextPeriodDate(),
                data.getPregnancyTestDay()
        );
    }
}
