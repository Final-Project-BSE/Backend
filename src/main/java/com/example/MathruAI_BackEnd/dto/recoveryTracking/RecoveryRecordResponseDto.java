package com.example.MathruAI_BackEnd.dto.recoveryTracking;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecoveryRecordResponseDto {
    private Long id;
    private Long patientId;
    private Integer dayNumber;
    private List<String> completedTaskIds;
    private String dailyNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
