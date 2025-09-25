package com.example.MathruAI_BackEnd.dto.questionDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormSubmissionDto {
    private int userId;
    private int formId;
    private List<UserResponseDto> responses;
}