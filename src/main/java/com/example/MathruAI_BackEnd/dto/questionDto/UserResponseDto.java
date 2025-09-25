package com.example.MathruAI_BackEnd.dto.questionDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    private int responseId;
    private int userId;
    private int formId;
    private int questionId;
    private String responseText;
    private LocalDateTime submittedAt;
}