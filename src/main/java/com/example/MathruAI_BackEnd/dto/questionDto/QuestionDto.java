package com.example.MathruAI_BackEnd.dto.questionDto;

import com.example.MathruAI_BackEnd.entity.questions.Answer;
import com.example.MathruAI_BackEnd.entity.questions.Options;
import com.example.MathruAI_BackEnd.entity.questions.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDto {
    private int questionId;
    private String questionText;
    private Set<QuestionType> questionTypes;
    private List<Options> optionsList;
    private List<Answer> answers;
}
