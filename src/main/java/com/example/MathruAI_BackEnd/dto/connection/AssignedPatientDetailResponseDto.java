package com.example.MathruAI_BackEnd.dto.connection;

import com.example.MathruAI_BackEnd.entity.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
public class AssignedPatientDetailResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String nationalIdNumber;
    private String address;
    private String profileImageUrl;

    private String area;
    private String district;
    private String mohArea;
    private Double latitude;
    private Double longitude;

    private Long assignedMidwifeId;
    private Set<Role> roles;

    private boolean canEditProfile;
    private boolean canViewHealthRecords;
    private boolean canViewFertility;
    private boolean canViewRiskPredictions;
    private boolean canManageAppointments;
}