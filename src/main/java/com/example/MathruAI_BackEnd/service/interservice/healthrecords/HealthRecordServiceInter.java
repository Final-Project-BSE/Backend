package com.example.MathruAI_BackEnd.service.interservice.healthrecords;

import com.example.MathruAI_BackEnd.dto.healthrecords.HealthCategoryResponseDto;
import com.example.MathruAI_BackEnd.dto.healthrecords.HealthRecordRequestDto;
import com.example.MathruAI_BackEnd.dto.healthrecords.HealthRecordResponseDto;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface HealthRecordServiceInter {

    List<HealthCategoryResponseDto> getAllCategoriesWithCounts(String email);

    List<HealthRecordResponseDto> getRecordsByCategory(String email, UUID categoryId);

    HealthRecordResponseDto createRecord(String email, UUID categoryId, HealthRecordRequestDto request)
            throws IOException;

    HealthRecordResponseDto getRecordDetails(UUID recordId);

    HealthRecordResponseDto updateRecord(UUID recordId, HealthRecordRequestDto request)
            throws IOException;

    void deleteRecord(UUID recordId) throws IOException;

    List<HealthCategoryResponseDto> getCategoriesForAssignedPatient(Long midwifeId, Long patientId);

    List<HealthRecordResponseDto> getRecordsByCategoryForAssignedPatient(Long midwifeId, Long patientId, UUID categoryId);

    HealthRecordResponseDto getRecordDetailForAssignedPatient(Long midwifeId, Long patientId, UUID recordId);

    HealthRecordResponseDto createRecordForAssignedPatient(
            Long midwifeId,
            Long patientId,
            UUID categoryId,
            HealthRecordRequestDto request
    ) throws IOException;

    void deleteRecordForAssignedPatient(
            Long midwifeId,
            Long patientId,
            UUID recordId
    ) throws IOException;
}