package com.example.MathruAI_BackEnd.dto.healthrecords;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecordFileResponseDto {
    private String id;
    private String fileName;
    private String fileType;
    private String fileUrl;
    private Long fileSize;
}