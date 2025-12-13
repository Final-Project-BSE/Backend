package com.example.MathruAI_BackEnd.entity.reproductive.healthmonitoring;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HealthData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
