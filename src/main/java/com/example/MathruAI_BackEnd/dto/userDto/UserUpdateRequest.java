package com.example.MathruAI_BackEnd.dto.userDto;

import com.example.MathruAI_BackEnd.entity.Role;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class UserUpdateRequest {
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

    private Set<Role> roles;
}