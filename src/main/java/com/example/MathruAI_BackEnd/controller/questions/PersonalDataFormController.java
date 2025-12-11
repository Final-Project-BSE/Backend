package com.example.MathruAI_BackEnd.controller.questions;

import com.example.MathruAI_BackEnd.dto.questionDto.PersonalDataFormDto;
import com.example.MathruAI_BackEnd.service.impl.questions.PersonalDataFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/personal-data-form")
public class PersonalDataFormController {

    private final PersonalDataFormService formService;

    @Autowired
    public PersonalDataFormController(PersonalDataFormService formService) {
        this.formService = formService;
    }

    @GetMapping
    public ResponseEntity<List<PersonalDataFormDto>> getAllForms() {
        return ResponseEntity.ok(formService.getAllActiveForms());
    }

    @GetMapping("/{formId}")
    public ResponseEntity<PersonalDataFormDto> getFormWithQuestions(@PathVariable int formId) {
        PersonalDataFormDto form = formService.getFormWithQuestions(formId);
        if (form == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(form);
    }

    @PostMapping
    public ResponseEntity<PersonalDataFormDto> createForm(@RequestBody PersonalDataFormDto formDto) {
        return ResponseEntity.ok(formService.createForm(formDto));
    }
}
