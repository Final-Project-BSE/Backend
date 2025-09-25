package com.example.MathruAI_BackEnd.service.impl.questions;

import com.example.MathruAI_BackEnd.dto.questionDto.AnswerDto;
import com.example.MathruAI_BackEnd.entity.questions.Answer;
import com.example.MathruAI_BackEnd.repository.questions.AnswerRepository;
import com.example.MathruAI_BackEnd.service.interservice.questions.AnswerServiceInter;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AnswerServiceImpl implements AnswerServiceInter {

    private final AnswerRepository answerRepository;

    public AnswerServiceImpl(AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
    }

    @Override
    public List<AnswerDto> getAllAnswers() {
        List<Answer> answers = answerRepository.findAll();
        List<AnswerDto> answerDtos = new ArrayList<>();

        for (Answer a : answers) {
            AnswerDto dto = new AnswerDto();
            dto.setAnswerId(a.getAnswerId());
            dto.setAnswerText(a.getAnswerText());
            dto.setCreatedAt(a.getCreatedAt());

            answerDtos.add(dto);
        }

        return answerDtos;
    }

    @Override
    public AnswerDto createAnswers(AnswerDto answerDto) {
        Answer answer = new Answer();
        answer.setAnswerText(answerDto.getAnswerText());
        answer.setCreatedAt(LocalDateTime.now());

        Answer savedAnswer = answerRepository.save(answer);

        return answerDto;
    }

    @Override
    public AnswerDto updateAnswers(int id, AnswerDto answerDto) {
        Answer answer = answerRepository.findById(id).get();

        answer.setAnswerText(answerDto.getAnswerText());

        Answer updatedAnswer = answerRepository.save(answer);

        AnswerDto updatedDto = new AnswerDto();
        updatedDto.setAnswerId(updatedAnswer.getAnswerId());
        updatedDto.setAnswerText(updatedAnswer.getAnswerText());
        updatedDto.setCreatedAt(updatedAnswer.getCreatedAt());

        return updatedDto;
    }

    @Override
    public void deleteAnswers(int id) {
        answerRepository.deleteById(id);
    }
}
