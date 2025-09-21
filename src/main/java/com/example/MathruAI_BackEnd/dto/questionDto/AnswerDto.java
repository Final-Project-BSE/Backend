package com.example.MathruAI_BackEnd.dto.questionDto;

import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerDto {
    private int answerId;
    private String answerText;
    private LocalDateTime createdAt;
}
