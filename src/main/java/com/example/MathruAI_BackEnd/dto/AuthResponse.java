package com.example.MathruAI_BackEnd.dto;

import com.example.MathruAI_BackEnd.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String email;
    private Set<Role> roles;
}