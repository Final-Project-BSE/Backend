package com.example.MathruAI_BackEnd.controller.reproductive.healthmonitoring;

import com.example.MathruAI_BackEnd.dto.reproductiveDto.healthmonitoring.HealthDataDto;
import com.example.MathruAI_BackEnd.repository.reproductive.HealthMonitoring.HealthDataRepository;
import com.example.MathruAI_BackEnd.service.impl.reproductive.healthmonitoring.HealthDataServiceImpl;
import com.example.MathruAI_BackEnd.service.interservice.healthmonitoring.HealthDataServiceInter;
import jdk.jfr.Registered;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class HealthDataController{

    private final HealthDataServiceImpl healthDataService;

    @Autowired
    public HealthDataController(HealthDataServiceImpl healthDataService){
        this.healthDataService = healthDataService;
    }

    @GetMapping
    public ResponseEntity<List<HealthDataDto>> getall(){
        return ResponseEntity.ok(healthDataService.getAllHealthData());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HealthDataDto> getById(int id){
        return ResponseEntity.ok(healthDataService.getHealthDataById(id));
    }

    @PostMapping
    public ResponseEntity <HealthDataDto> createData(HealthDataDto healthDataDto){
        return ResponseEntity.ok(healthDataService.createHealthData(healthDataDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HealthDataDto> updateData(int id, HealthDataDto healthDataDto){
        return ResponseEntity.ok(healthDataService.updateHealthData(id, healthDataDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteData(int id){
        healthDataService.deleteHealthData(id);
        return ResponseEntity.noContent().build();
    }

}
