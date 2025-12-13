package com.example.MathruAI_BackEnd.service.interservice.healthmonitoring;

import com.example.MathruAI_BackEnd.dto.reproductiveDto.healthmonitoring.HealthDataDto;

import java.util.List;

public interface HealthDataServiceInter {
    List<HealthDataDto> getAllHealthData();
    HealthDataDto getHealthDataById(int id);
    HealthDataDto createHealthData(HealthDataDto healthDataDto);
    HealthDataDto updateHealthData(int id, HealthDataDto healthDataDto);
    void deleteHealthData(int id);
}
