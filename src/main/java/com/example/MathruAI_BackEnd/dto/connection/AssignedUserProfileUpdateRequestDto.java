package com.example.MathruAI_BackEnd.dto.connection;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AssignedUserProfileUpdateRequestDto {
    private String firstName;
    private String lastName;
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
}