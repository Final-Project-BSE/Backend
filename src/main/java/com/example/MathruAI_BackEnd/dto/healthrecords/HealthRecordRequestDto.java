package com.example.MathruAI_BackEnd.dto.healthrecords;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthRecordRequestDto {
    private String name;
    private String date; // String to handle different format from frontend if needed, or LocalDate
    private String description;
    private List<MultipartFile> files;
}
