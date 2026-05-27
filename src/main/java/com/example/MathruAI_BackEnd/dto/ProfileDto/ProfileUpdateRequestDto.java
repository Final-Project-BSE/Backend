package com.example.MathruAI_BackEnd.dto.ProfileDto;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileUpdateRequestDto {
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