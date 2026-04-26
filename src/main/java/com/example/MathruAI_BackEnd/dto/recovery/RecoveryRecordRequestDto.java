package com.example.MathruAI_BackEnd.dto.recovery;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecoveryRecordRequestDto {
    private Long patientId;
    private Integer dayNumber;
    private List<String> completedTaskIds;
    private String dailyNotes;
}
