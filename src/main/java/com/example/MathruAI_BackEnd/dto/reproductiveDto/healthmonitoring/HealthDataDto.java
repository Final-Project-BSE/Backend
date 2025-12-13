package com.example.MathruAI_BackEnd.dto.reproductiveDto.healthmonitoring;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HealthDataDto {
    private int id;
    private int age;
    private int systolicBP;
    private int diastolicBP;
    private double bloodSugar;
    private double bodyTemp;
    private int heartRate;
    private double weight;
    private double height;
    private double bmi;
    private int previousComplications;
    private int preexistingDiabetes;
    private int gestationalDiabetes;
    private int mentalHealth;
}
