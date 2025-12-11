package com.example.MathruAI_BackEnd.service.impl.questions;

import com.example.MathruAI_BackEnd.dto.questionDto.OptionDto;

import com.example.MathruAI_BackEnd.entity.questions.Options;
import com.example.MathruAI_BackEnd.repository.questions.OptionsRepository;
import com.example.MathruAI_BackEnd.service.interservice.questions.OptionsServiceInter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OptionsServiceImpl implements OptionsServiceInter {

    private final OptionsRepository optionRepository;

    public OptionsServiceImpl(OptionsRepository optionRepository) {
        this.optionRepository = optionRepository;
    }

    @Override
    public List<OptionDto> getAllOptions() {
        List<Options> options = optionRepository.findAll();
        List<OptionDto> optionDtos = new ArrayList<>();

        for (Options o : options) {
            OptionDto dto = new OptionDto();
            dto.setOptionId(o.getOptionId());
            dto.setOptionText(o.getOptionText());

            optionDtos.add(dto);
        }

        return optionDtos;
    }

    @Override
    public OptionDto createOptions(OptionDto optionDto) {
        Options option = new Options();
        option.setOptionText(optionDto.getOptionText());

        Options savedOption = optionRepository.save(option);

        optionDto.setOptionId(savedOption.getOptionId());
        return optionDto;
    }

    @Override
    public OptionDto updateOptions(int id, OptionDto optionDto) {
        Options optionalOption = optionRepository.findById(id).get();

        optionalOption.setOptionText(optionDto.getOptionText());

        Options updatedOption = optionRepository.save(optionalOption);

        OptionDto updatedDto = new OptionDto();
        updatedDto.setOptionId(updatedOption.getOptionId());
        updatedDto.setOptionText(updatedOption.getOptionText());

        return updatedDto;
    }

    @Override
    public void deleteOptions(int id) {
        optionRepository.deleteById(id);
    }
}
