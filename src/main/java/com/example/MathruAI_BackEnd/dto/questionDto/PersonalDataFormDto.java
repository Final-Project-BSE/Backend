package com.example.MathruAI_BackEnd.dto.questionDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonalDataFormDto {
    private int formId;
    private String formName;
    private String description;
    private boolean isActive;
    private List<QuestionDto> questions;
}