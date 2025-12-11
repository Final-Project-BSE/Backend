package com.example.MathruAI_BackEnd.service.impl.questions;

import com.example.MathruAI_BackEnd.dto.questionDto.PersonalDataFormDto;
import com.example.MathruAI_BackEnd.dto.questionDto.QuestionDto;
import com.example.MathruAI_BackEnd.entity.questions.PersonalDataForm;
import com.example.MathruAI_BackEnd.entity.questions.Question;
import com.example.MathruAI_BackEnd.repository.questions.PersonalDataFormRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PersonalDataFormService {

    private final PersonalDataFormRepository formRepository;

    public PersonalDataFormService(PersonalDataFormRepository formRepository) {
        this.formRepository = formRepository;
    }

    public List<PersonalDataFormDto> getAllActiveForms() {
        List<PersonalDataForm> forms = formRepository.findByIsActiveTrue();
        List<PersonalDataFormDto> formDtos = new ArrayList<>();

        for (PersonalDataForm form : forms) {
            PersonalDataFormDto dto = new PersonalDataFormDto();
            dto.setFormId(form.getFormId());
            dto.setFormName(form.getFormName());
            dto.setDescription(form.getDescription());
            dto.setActive(form.isActive());
            formDtos.add(dto);
        }
        return formDtos;
    }

    public PersonalDataFormDto getFormWithQuestions(int formId) {
        PersonalDataForm form = formRepository.findById(formId).orElse(null);
        if (form == null) return null;

        PersonalDataFormDto dto = new PersonalDataFormDto();
        dto.setFormId(form.getFormId());
        dto.setFormName(form.getFormName());
        dto.setDescription(form.getDescription());
        dto.setActive(form.isActive());

        List<QuestionDto> questionDtos = new ArrayList<>();
        for (Question question : form.getQuestions()) {
            QuestionDto questionDto = new QuestionDto();
            questionDto.setQuestionId(question.getQuestionId());
            questionDto.setQuestionText(question.getQuestionText());
            questionDto.setRequired(question.isRequired());
            questionDto.setQuestionTypes(question.getQuestionTypes());
            questionDto.setOptionsList(question.getOptionsList());
            questionDto.setFormId(formId);
            questionDtos.add(questionDto);
        }
        dto.setQuestions(questionDtos);

        return dto;
    }

    public PersonalDataFormDto createForm(PersonalDataFormDto formDto) {
        PersonalDataForm form = new PersonalDataForm();
        form.setFormName(formDto.getFormName());
        form.setDescription(formDto.getDescription());
        form.setActive(formDto.isActive());

        PersonalDataForm savedForm = formRepository.save(form);
        formDto.setFormId(savedForm.getFormId());
        return formDto;
    }
}