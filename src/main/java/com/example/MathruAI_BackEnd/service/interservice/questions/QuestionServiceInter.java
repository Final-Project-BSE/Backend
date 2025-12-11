package com.example.MathruAI_BackEnd.service.interservice.questions;

import com.example.MathruAI_BackEnd.dto.questionDto.QuestionDto;

import java.util.List;

public interface QuestionServiceInter {
    List<QuestionDto> getAllQuestions();
    QuestionDto createQuestions(QuestionDto questionDto);
    QuestionDto updateQuestions(int id, QuestionDto questionDto);
    void deleteQuestions(int id);
}
