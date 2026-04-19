package com.example.MathruAI_BackEnd.dto.ProfileDto;

import com.example.MathruAI_BackEnd.entity.Role;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResponseDto {
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
}