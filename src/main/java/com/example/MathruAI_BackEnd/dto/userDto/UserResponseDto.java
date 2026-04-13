package com.example.MathruAI_BackEnd.dto.userDto;

import com.example.MathruAI_BackEnd.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
@AllArgsConstructor
public class UserResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate dateOfBirth;
    private String nationalIdNumber;
    private String address;
    private String profileImageUrl;
    private Set<Role> roles;
}