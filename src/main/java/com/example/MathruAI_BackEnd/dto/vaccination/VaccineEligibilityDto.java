package com.example.MathruAI_BackEnd.dto.vaccination;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VaccineEligibilityDto {
    private String vaccineName;
    private String vaccineType;
    private String dose;
    private long eligiblePatients;
}