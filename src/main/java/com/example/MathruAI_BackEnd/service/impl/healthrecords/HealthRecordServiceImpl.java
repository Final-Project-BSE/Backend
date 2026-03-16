package com.example.MathruAI_BackEnd.service.impl.healthrecords;

import com.example.MathruAI_BackEnd.dto.healthrecords.HealthCategoryResponseDto;
import com.example.MathruAI_BackEnd.dto.healthrecords.HealthRecordRequestDto;
import com.example.MathruAI_BackEnd.dto.healthrecords.HealthRecordResponseDto;
import com.example.MathruAI_BackEnd.entity.User;
import com.example.MathruAI_BackEnd.entity.healthrecords.HealthCategory;
import com.example.MathruAI_BackEnd.entity.healthrecords.HealthRecord;
import com.example.MathruAI_BackEnd.entity.healthrecords.RecordFile;
import com.example.MathruAI_BackEnd.repository.healthrecords.HealthCategoryRepository;
import com.example.MathruAI_BackEnd.repository.healthrecords.HealthRecordRepository;
import com.example.MathruAI_BackEnd.repository.healthrecords.RecordFileRepository;
import com.example.MathruAI_BackEnd.repository.UserRepository;
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
        User user = userRepository.findByEmail(email).orElseThrow();
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
        User user = userRepository.findByEmail(email).orElseThrow();
        HealthCategory category = categoryRepository.findById(categoryId).orElseThrow();

        List<HealthRecord> records = recordRepository.findByUserAndCategory(user, category);
        return records.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public HealthRecordResponseDto createRecord(String email, UUID categoryId, HealthRecordRequestDto request)
            throws IOException {
        User user = userRepository.findByEmail(email).orElseThrow();
        HealthCategory category = categoryRepository.findById(categoryId).orElseThrow();

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
            savedRecord.setFiles(files);
        }

        return mapToResponse(savedRecord);
    }

    @Override
    public HealthRecordResponseDto getRecordDetails(UUID recordId) {
        HealthRecord record = recordRepository.findById(recordId).orElseThrow();
        return mapToResponse(record);
    }

    @Override
    @Transactional
    public HealthRecordResponseDto updateRecord(UUID recordId, HealthRecordRequestDto request) throws IOException {
        HealthRecord record = recordRepository.findById(recordId).orElseThrow();

        record.setName(request.getName());
        record.setDate(LocalDate.parse(request.getDate()));
        record.setDescription(request.getDescription());

        // Handle file updates (simplified: just add new ones for now, or clear and
        // replace)
        if (request.getFiles() != null && !request.getFiles().isEmpty()) {
            for (MultipartFile file : request.getFiles()) {
                String fileUrl = fileUploadService.storeFile(file);
                RecordFile recordFile = RecordFile.builder()
                        .record(record)
                        .fileName(file.getOriginalFilename())
                        .fileType(file.getContentType())
                        .fileUrl(fileUrl)
                        .fileSize(file.getSize())
                        .build();
                record.getFiles().add(recordFile);
            }
        }

        return mapToResponse(recordRepository.save(record));
    }

    @Override
    @Transactional
    public void deleteRecord(UUID recordId) throws IOException {
        HealthRecord record = recordRepository.findById(recordId).orElseThrow();

        // Delete files from storage
        for (RecordFile file : record.getFiles()) {
            // Need to extract filename from URL if it's stored that way
            String fileName = file.getFileUrl().substring(file.getFileUrl().lastIndexOf("/") + 1);
            fileUploadService.deleteFile(fileName);
        }

        recordRepository.delete(record);
    }

    private HealthRecordResponseDto mapToResponse(HealthRecord record) {
        return HealthRecordResponseDto.builder()
                .id(record.getId())
                .name(record.getName())
                .date(record.getDate())
                .description(record.getDescription())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .categoryName(record.getCategory().getName())
                .categoryId(record.getCategory().getId())
                .files(record.getFiles() != null ? record.getFiles().stream()
                        .map(f -> HealthRecordResponseDto.RecordFileResponseDto.builder()
                                .id(f.getId())
                                .fileName(f.getFileName())
                                .fileType(f.getFileType())
                                .fileUrl(f.getFileUrl())
                                .fileSize(f.getFileSize())
                                .build())
                        .collect(Collectors.toList()) : new ArrayList<>())
                .build();
    }
}
