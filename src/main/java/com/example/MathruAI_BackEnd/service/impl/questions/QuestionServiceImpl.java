package com.example.MathruAI_BackEnd.service.impl.questions;

import com.example.MathruAI_BackEnd.dto.questionDto.QuestionDto;
import com.example.MathruAI_BackEnd.entity.questions.Question;
import com.example.MathruAI_BackEnd.repository.questions.QuestionRepository;
import com.example.MathruAI_BackEnd.service.interservice.questions.QuestionServiceInter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionServiceInter {

    private QuestionRepository questionRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository){
        this.questionRepository = questionRepository;
    }

    @Override
    public List<QuestionDto> getAllQuestions(){
        List<Question> questions = questionRepository.findAll();

        List<QuestionDto> questionDtos = new ArrayList<>();

        for(Question q : questions){
            QuestionDto questionDto = new QuestionDto();
            questionDto.setQuestionId(q.getQuestionId());
            questionDto.setQuestionText(q.getQuestionText());
            questionDto.setQuestionTypes(q.getQuestionTypes());
            questionDto.setAnswers(q.getAnswers());
            questionDto.setOptionsList(q.getOptionsList());

            questionDtos.add(questionDto);
        }
        return questionDtos;
    }

    @Override
    public QuestionDto createQuestions(QuestionDto questionDto) {

        Question question = new Question();
        question.setQuestionText(questionDto.getQuestionText());
        question.setQuestionTypes(questionDto.getQuestionTypes());
        question.setAnswers(questionDto.getAnswers());
        question.setOptionsList(questionDto.getOptionsList());

        Question savedQuestion = questionRepository.save(question);

        return questionDto;
    }

    @Override
    public QuestionDto updateQuestions(int id, QuestionDto questionDto) {

        Question question = questionRepository.findById(id).get();

        question.setQuestionText(questionDto.getQuestionText());
        question.setQuestionTypes(questionDto.getQuestionTypes());
        question.setAnswers(questionDto.getAnswers());
        question.setOptionsList(questionDto.getOptionsList());

        Question updatedQuestion = questionRepository.save(question);

        return questionDto;
    }

    @Override
    public void deleteQuestions(int id) {
        questionRepository.deleteById(id);
    }

}
