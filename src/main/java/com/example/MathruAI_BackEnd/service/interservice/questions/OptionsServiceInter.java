package com.example.MathruAI_BackEnd.service.interservice.questions;

import com.example.MathruAI_BackEnd.dto.questionDto.OptionDto;
import com.example.MathruAI_BackEnd.dto.questionDto.QuestionDto;

import java.util.List;

public interface OptionsServiceInter {
    List<OptionDto> getAllOptions();
    OptionDto createOptions(OptionDto optionDto);
    OptionDto updateOptions(int id, OptionDto optionDto);
    void deleteOptions(int id);
}
