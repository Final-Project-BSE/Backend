package com.example.MathruAI_BackEnd.service.impl.questions;

import com.example.MathruAI_BackEnd.dto.questionDto.FormSubmissionDto;
import com.example.MathruAI_BackEnd.dto.questionDto.UserResponseDto;
import com.example.MathruAI_BackEnd.entity.questions.UserResponse;
import com.example.MathruAI_BackEnd.repository.questions.UserResponseRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserResponseService {

    private final UserResponseRepository responseRepository;

    public UserResponseService(UserResponseRepository responseRepository) {
        this.responseRepository = responseRepository;
    }

    public boolean submitForm(FormSubmissionDto submissionDto) {
        // Check if user already submitted this form
        boolean alreadySubmitted = responseRepository.existsByUserIdAndFormId(
                submissionDto.getUserId(), submissionDto.getFormId());

        if (alreadySubmitted) {
            return false; // User already submitted
        }

        // Save all responses
        for (UserResponseDto responseDto : submissionDto.getResponses()) {
            UserResponse response = new UserResponse();
            response.setUserId(submissionDto.getUserId());
            response.setFormId(submissionDto.getFormId());
            response.setQuestionId(responseDto.getQuestionId());
            response.setResponseText(responseDto.getResponseText());
            response.setSubmittedAt(LocalDateTime.now());

            responseRepository.save(response);
        }
        return true;
    }

    public List<UserResponseDto> getUserResponses(int userId, int formId) {
        List<UserResponse> responses = responseRepository.findByUserIdAndFormId(userId, formId);
        List<UserResponseDto> responseDtos = new ArrayList<>();

        for (UserResponse response : responses) {
            UserResponseDto dto = new UserResponseDto();
            dto.setResponseId(response.getResponseId());
            dto.setUserId(response.getUserId());
            dto.setFormId(response.getFormId());
            dto.setQuestionId(response.getQuestionId());
            dto.setResponseText(response.getResponseText());
            dto.setSubmittedAt(response.getSubmittedAt());
            responseDtos.add(dto);
        }
        return responseDtos;
    }
}
