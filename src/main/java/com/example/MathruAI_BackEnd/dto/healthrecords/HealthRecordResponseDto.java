package com.example.MathruAI_BackEnd.dto.healthrecords;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthRecordResponseDto {
    private UUID id;
    private String name;
    private LocalDate date;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String categoryName;
    private UUID categoryId;
    private List<RecordFileResponseDto> files;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecordFileResponseDto {
        private UUID id;
        private String fileName;
        private String fileType;
        private String fileUrl;
        private Long fileSize;
    }
}
