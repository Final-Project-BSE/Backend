package com.example.MathruAI_BackEnd.service.impl.questions;

import com.example.MathruAI_BackEnd.dto.questionDto.QuestionDto;
import com.example.MathruAI_BackEnd.entity.questions.Options;
import com.example.MathruAI_BackEnd.entity.questions.Question;
import com.example.MathruAI_BackEnd.entity.questions.PersonalDataForm;
import com.example.MathruAI_BackEnd.repository.questions.QuestionRepository;
import com.example.MathruAI_BackEnd.repository.questions.OptionsRepository;
import com.example.MathruAI_BackEnd.repository.questions.PersonalDataFormRepository;
import com.example.MathruAI_BackEnd.service.interservice.questions.QuestionServiceInter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionServiceInter {

    private final QuestionRepository questionRepository;
    private final OptionsRepository optionsRepository;
    private final PersonalDataFormRepository formRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository,
                               OptionsRepository optionsRepository,
                               PersonalDataFormRepository formRepository) {
        this.questionRepository = questionRepository;
        this.optionsRepository = optionsRepository;
        this.formRepository = formRepository;
    }

    @Override
    public List<QuestionDto> getAllQuestions() {
        List<Question> questions = questionRepository.findAll();
        List<QuestionDto> questionDtos = new ArrayList<>();

        for (Question q : questions) {
            QuestionDto questionDto = new QuestionDto();
            questionDto.setQuestionId(q.getQuestionId());
            questionDto.setQuestionText(q.getQuestionText());
            questionDto.setRequired(q.isRequired());
            questionDto.setQuestionTypes(q.getQuestionTypes());
            questionDto.setOptionsList(q.getOptionsList());
            if (q.getForm() != null) {
                questionDto.setFormId(q.getForm().getFormId());
            }
            questionDtos.add(questionDto);
        }
        return questionDtos;
    }

    @Override
    public QuestionDto createQuestions(QuestionDto questionDto) {
        Question question = new Question();
        question.setQuestionText(questionDto.getQuestionText());
        question.setRequired(questionDto.isRequired());
        question.setQuestionTypes(questionDto.getQuestionTypes());

        // Handle form relationship
        if (questionDto.getFormId() > 0) {
            PersonalDataForm form = formRepository.findById(questionDto.getFormId()).orElse(null);
            question.setForm(form);
        }

        // Handle existing options (fetch from database to avoid detached entity error)
        List<Options> managedOptions = new ArrayList<>();
        if (questionDto.getOptionsList() != null && !questionDto.getOptionsList().isEmpty()) {
            for (Options option : questionDto.getOptionsList()) {
                if (option.getOptionId() > 0) {
                    // Fetch existing option from database
                    Options existingOption = optionsRepository.findById(option.getOptionId()).orElse(null);
                    if (existingOption != null) {
                        managedOptions.add(existingOption);
                    }
                } else {
                    // Create new option
                    Options newOption = new Options();
                    newOption.setOptionText(option.getOptionText());
                    Options savedOption = optionsRepository.save(newOption);
                    managedOptions.add(savedOption);
                }
            }
        }
        question.setOptionsList(managedOptions);

        Question savedQuestion = questionRepository.save(question);

        // Update DTO with saved question info
        questionDto.setQuestionId(savedQuestion.getQuestionId());
        return questionDto;
    }

    @Override
    public QuestionDto updateQuestions(int id, QuestionDto questionDto) {
        Question question = questionRepository.findById(id).orElse(null);
        if (question == null) {
            return null;
        }

        question.setQuestionText(questionDto.getQuestionText());
        question.setRequired(questionDto.isRequired());
        question.setQuestionTypes(questionDto.getQuestionTypes());

        // Handle form relationship
        if (questionDto.getFormId() > 0) {
            PersonalDataForm form = formRepository.findById(questionDto.getFormId()).orElse(null);
            question.setForm(form);
        }

        // Handle options update
        List<Options> managedOptions = new ArrayList<>();
        if (questionDto.getOptionsList() != null && !questionDto.getOptionsList().isEmpty()) {
            for (Options option : questionDto.getOptionsList()) {
                if (option.getOptionId() > 0) {
                    Options existingOption = optionsRepository.findById(option.getOptionId()).orElse(null);
                    if (existingOption != null) {
                        managedOptions.add(existingOption);
                    }
                } else {
                    Options newOption = new Options();
                    newOption.setOptionText(option.getOptionText());
                    Options savedOption = optionsRepository.save(newOption);
                    managedOptions.add(savedOption);
                }
            }
        }
        question.setOptionsList(managedOptions);
        
        Question updatedQuestion = questionRepository.save(question);

        // Update DTO with updated question info
        questionDto.setQuestionId(updatedQuestion.getQuestionId());
        return questionDto;
    }

    @Override
    public void deleteQuestions(int id) {
        questionRepository.deleteById(id);
    }
}
