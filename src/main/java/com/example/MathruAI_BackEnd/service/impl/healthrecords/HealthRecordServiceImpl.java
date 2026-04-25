package com.example.MathruAI_BackEnd.service.impl.healthrecords;

import com.example.MathruAI_BackEnd.dto.healthrecords.HealthCategoryResponseDto;
import com.example.MathruAI_BackEnd.dto.healthrecords.HealthRecordRequestDto;
import com.example.MathruAI_BackEnd.dto.healthrecords.HealthRecordResponseDto;
import com.example.MathruAI_BackEnd.dto.healthrecords.RecordFileResponseDto;
import com.example.MathruAI_BackEnd.entity.Role;
import com.example.MathruAI_BackEnd.entity.User;
import com.example.MathruAI_BackEnd.entity.healthrecords.HealthCategory;
import com.example.MathruAI_BackEnd.entity.healthrecords.HealthRecord;
import com.example.MathruAI_BackEnd.entity.healthrecords.RecordFile;
import com.example.MathruAI_BackEnd.repository.UserRepository;
import com.example.MathruAI_BackEnd.repository.healthrecords.HealthCategoryRepository;
import com.example.MathruAI_BackEnd.repository.healthrecords.HealthRecordRepository;
import com.example.MathruAI_BackEnd.repository.healthrecords.RecordFileRepository;
import com.example.MathruAI_BackEnd.service.interservice.healthrecords.HealthRecordServiceInter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HealthRecordServiceImpl implements HealthRecordServiceInter {

    private final HealthRecordRepository recordRepository;
    private final HealthCategoryRepository categoryRepository;
    private final RecordFileRepository fileRepository;
    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;

    @Override
    public List<HealthCategoryResponseDto> getAllCategoriesWithCounts(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found."));
        List<HealthCategory> categories = categoryRepository.findAll();

        return categories.stream().map(cat -> {
            long count = recordRepository.findByUserAndCategory(user, cat).size();
            return HealthCategoryResponseDto.builder()
                    .id(cat.getId())
                    .slug(cat.getSlug())
                    .name(cat.getName())
                    .icon(cat.getIcon())
                    .colorClass(cat.getColorClass())
                    .recordCount(count)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public List<HealthRecordResponseDto> getRecordsByCategory(String email, UUID categoryId) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found."));
        HealthCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found."));

        List<HealthRecord> records = recordRepository.findByUserAndCategory(user, category);
        return records.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public HealthRecordResponseDto createRecord(String email, UUID categoryId, HealthRecordRequestDto request)
            throws IOException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found."));
        HealthCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found."));

        HealthRecord record = HealthRecord.builder()
                .user(user)
                .category(category)
                .name(request.getName())
                .date(LocalDate.parse(request.getDate()))
                .description(request.getDescription())
                .build();

        HealthRecord savedRecord = recordRepository.save(record);

        if (request.getFiles() != null && !request.getFiles().isEmpty()) {
            List<RecordFile> files = new ArrayList<>();
            for (MultipartFile file : request.getFiles()) {
                String fileUrl = fileUploadService.storeFile(file);
                RecordFile recordFile = RecordFile.builder()
                        .record(savedRecord)
                        .fileName(file.getOriginalFilename())
                        .fileType(file.getContentType())
                        .fileUrl(fileUrl)
                        .fileSize(file.getSize())
                        .build();
                files.add(recordFile);
            }
            fileRepository.saveAll(files);
        }

        return getRecordDetails(savedRecord.getId());
    }

    @Override
    public HealthRecordResponseDto getRecordDetails(UUID recordId) {
        HealthRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Health record not found."));
        return mapToResponse(record);
    }

    @Override
    @Transactional
    public HealthRecordResponseDto updateRecord(UUID recordId, HealthRecordRequestDto request) throws IOException {
        HealthRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Health record not found."));

        if (request.getName() != null) {
            record.setName(request.getName());
        }
        if (request.getDate() != null) {
            record.setDate(LocalDate.parse(request.getDate()));
        }
        if (request.getDescription() != null) {
            record.setDescription(request.getDescription());
        }

        HealthRecord updatedRecord = recordRepository.save(record);

        if (request.getFiles() != null && !request.getFiles().isEmpty()) {
            List<RecordFile> files = new ArrayList<>();
            for (MultipartFile file : request.getFiles()) {
                String fileUrl = fileUploadService.storeFile(file);
                RecordFile recordFile = RecordFile.builder()
                        .record(updatedRecord)
                        .fileName(file.getOriginalFilename())
                        .fileType(file.getContentType())
                        .fileUrl(fileUrl)
                        .fileSize(file.getSize())
                        .build();
                files.add(recordFile);
            }
            fileRepository.saveAll(files);
        }

        return getRecordDetails(updatedRecord.getId());
    }

    @Override
    @Transactional
    public void deleteRecord(UUID recordId) throws IOException {
        HealthRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Health record not found."));

        if (record.getFiles() != null) {
            for (RecordFile file : record.getFiles()) {
                if (file.getFileUrl() != null && file.getFileUrl().contains("/api/health-records/files/")) {
                    String fileName = file.getFileUrl().substring(file.getFileUrl().lastIndexOf("/") + 1);
                    fileUploadService.deleteFile(fileName);
                }
            }
        }

        recordRepository.delete(record);
    }

    @Override
    public List<HealthCategoryResponseDto> getCategoriesForAssignedPatient(Long midwifeId, Long patientId) {
        User patient = getAssignedPatientOrThrow(midwifeId, patientId);
        List<HealthCategory> categories = categoryRepository.findAll();

        return categories.stream().map(cat -> {
            long count = recordRepository.findByUserAndCategory(patient, cat).size();
            return HealthCategoryResponseDto.builder()
                    .id(cat.getId())
                    .slug(cat.getSlug())
                    .name(cat.getName())
                    .icon(cat.getIcon())
                    .colorClass(cat.getColorClass())
                    .recordCount(count)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public List<HealthRecordResponseDto> getRecordsByCategoryForAssignedPatient(Long midwifeId, Long patientId, UUID categoryId) {
        User patient = getAssignedPatientOrThrow(midwifeId, patientId);
        HealthCategory category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found."));

        return recordRepository.findByUserAndCategory(patient, category)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public HealthRecordResponseDto getRecordDetailForAssignedPatient(Long midwifeId, Long patientId, UUID recordId) {
        User patient = getAssignedPatientOrThrow(midwifeId, patientId);

        HealthRecord record = recordRepository.findByIdAndUser(recordId, patient)
                .orElseThrow(() -> new RuntimeException("Health record not found for this patient."));

        return mapToResponse(record);
    }

    private User getAssignedPatientOrThrow(Long midwifeId, Long patientId) {
        User midwife = userRepository.findById(midwifeId)
                .orElseThrow(() -> new RuntimeException("Midwife not found."));

        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found."));

        if (midwife.getRoles() == null || !midwife.getRoles().contains(Role.MIDWIFE)) {
            throw new RuntimeException("User is not a midwife.");
        }

        if (patient.getAssignedMidwife() == null ||
                !patient.getAssignedMidwife().getId().equals(midwifeId)) {
            throw new RuntimeException("Patient is not assigned to this midwife.");
        }

        return patient;
    }

    private HealthRecordResponseDto mapToResponse(HealthRecord record) {
        List<RecordFileResponseDto> files = record.getFiles() == null
                ? List.of()
                : record.getFiles().stream()
                .map(this::mapFileToResponse)
                .collect(Collectors.toList());

        return HealthRecordResponseDto.builder()
                .id(record.getId())
                .name(record.getName())
                .date(record.getDate())
                .description(record.getDescription())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .categoryName(record.getCategory() != null ? record.getCategory().getName() : null)
                .categoryId(record.getCategory() != null ? record.getCategory().getId() : null)
                .files(files)
                .build();
    }

    private RecordFileResponseDto mapFileToResponse(RecordFile file) {
        return RecordFileResponseDto.builder()
                .id(file.getId() != null ? file.getId().toString() : null)
                .fileName(file.getFileName())
                .fileType(file.getFileType())
                .fileUrl(file.getFileUrl())
                .fileSize(file.getFileSize())
                .build();
    }


}