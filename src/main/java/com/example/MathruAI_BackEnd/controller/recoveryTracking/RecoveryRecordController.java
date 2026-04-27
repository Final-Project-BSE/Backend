package com.example.MathruAI_BackEnd.controller.recoveryTracking;

import com.example.MathruAI_BackEnd.dto.recoveryTracking.RecoveryRecordRequestDto;
import com.example.MathruAI_BackEnd.dto.recoveryTracking.RecoveryRecordResponseDto;
import com.example.MathruAI_BackEnd.service.interservice.recoveryTracking.RecoveryRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recovery-records")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Adjust based on your general security configuration
public class RecoveryRecordController {

    private final RecoveryRecordService recoveryRecordService;

    @PostMapping
    public ResponseEntity<RecoveryRecordResponseDto> saveOrUpdateRecord(@RequestBody RecoveryRecordRequestDto requestDto) {
        RecoveryRecordResponseDto savedRecord = recoveryRecordService.saveOrUpdateRecord(requestDto);
        return new ResponseEntity<>(savedRecord, HttpStatus.OK);
    }

    @GetMapping("/patient/{patientId}/day/{dayNumber}")
    public ResponseEntity<RecoveryRecordResponseDto> getRecordByPatientAndDay(
            @PathVariable Long patientId,
            @PathVariable Integer dayNumber) {
        RecoveryRecordResponseDto record = recoveryRecordService.getRecordByPatientAndDay(patientId, dayNumber);
        return ResponseEntity.ok(record);
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<RecoveryRecordResponseDto>> getAllRecordsForPatient(@PathVariable Long patientId) {
        List<RecoveryRecordResponseDto> records = recoveryRecordService.getAllRecordsForPatient(patientId);
        return ResponseEntity.ok(records);
    }
}
