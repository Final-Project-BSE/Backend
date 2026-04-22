package com.example.MathruAI_BackEnd.controller.connection;

import com.example.MathruAI_BackEnd.service.interservice.connection.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @GetMapping("/districts")
    public ResponseEntity<List<String>> getDistricts() {
        return ResponseEntity.ok(locationService.getAllDistricts());
    }

    @GetMapping("/moh-areas")
    public ResponseEntity<List<String>> getMohAreasByDistrict(@RequestParam String district) {
        return ResponseEntity.ok(locationService.getMohAreasByDistrict(district));
    }
}