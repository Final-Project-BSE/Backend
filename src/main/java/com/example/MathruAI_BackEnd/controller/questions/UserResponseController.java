package com.example.MathruAI_BackEnd.controller.questions;

import com.example.MathruAI_BackEnd.dto.questionDto.FormSubmissionDto;
import com.example.MathruAI_BackEnd.dto.questionDto.UserResponseDto;
import com.example.MathruAI_BackEnd.service.impl.questions.UserResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-response")
public class UserResponseController {

    private final UserResponseService responseService;

    @Autowired
    public UserResponseController(UserResponseService responseService) {
        this.responseService = responseService;
    }

    @PostMapping("/submit")
    public ResponseEntity<String> submitForm(@RequestBody FormSubmissionDto submissionDto) {
        boolean success = responseService.submitForm(submissionDto);
        if (success) {
            return ResponseEntity.ok("Form submitted successfully");
        } else {
            return ResponseEntity.badRequest().body("Form already submitted or error occurred");
        }
    }

    @GetMapping("/user/{userId}/form/{formId}")
    public ResponseEntity<List<UserResponseDto>> getUserResponses(
            @PathVariable int userId, @PathVariable int formId) {
        return ResponseEntity.ok(responseService.getUserResponses(userId, formId));
    }
}