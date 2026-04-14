package com.example.MathruAI_BackEnd.dto.connection;

import lombok.Data;

@Data
public class SendConnectionRequestDto {

    /**
     * Allowed values: EMAIL, AREA
     */
    private String method;

    /**
     * Required when method = EMAIL
     */
    private String targetEmail;

    /**
     * Required when method = AREA
     */
    private String targetArea;

    private String message;
}