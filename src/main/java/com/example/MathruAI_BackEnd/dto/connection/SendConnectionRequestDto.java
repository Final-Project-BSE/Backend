package com.example.MathruAI_BackEnd.dto.connection;

import lombok.Data;

@Data
public class SendConnectionRequestDto {
    private String method;
    private String targetEmail;
    private String targetArea;
    private String message;
}