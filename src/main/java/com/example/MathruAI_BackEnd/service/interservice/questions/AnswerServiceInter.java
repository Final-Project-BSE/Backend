package com.example.MathruAI_BackEnd.service.interservice.questions;

import com.example.MathruAI_BackEnd.dto.questionDto.AnswerDto;
import com.example.MathruAI_BackEnd.dto.questionDto.OptionDto;
import com.example.MathruAI_BackEnd.dto.questionDto.QuestionDto;

import java.util.List;

public interface AnswerServiceInter {
    List<AnswerDto> getAllAnswers();
    AnswerDto createAnswers(AnswerDto answerDto);
    AnswerDto updateAnswers(int id, AnswerDto answerDto);
    void deleteAnswers(int id);
}
