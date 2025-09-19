package com.example.MathruAI_BackEnd.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}