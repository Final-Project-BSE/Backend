package com.example.MathruAI_BackEnd.service.impl.recoveryTracking;

import com.example.MathruAI_BackEnd.dto.recoveryTracking.RecoveryRecordRequestDto;
import com.example.MathruAI_BackEnd.dto.recoveryTracking.RecoveryRecordResponseDto;
import com.example.MathruAI_BackEnd.entity.recoveryTracking.RecoveryRecord;
import com.example.MathruAI_BackEnd.repository.recoveryTracking.RecoveryRecordRepository;
import com.example.MathruAI_BackEnd.service.interservice.recoveryTracking.RecoveryRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecoveryRecordServiceImpl implements RecoveryRecordService {

    private final RecoveryRecordRepository recoveryRecordRepository;

    @Override
    @Transactional
    public RecoveryRecordResponseDto saveOrUpdateRecord(RecoveryRecordRequestDto requestDto) {
        if (requestDto.getDayNumber() < 1 || requestDto.getDayNumber() > 42) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Day number must be between 1 and 42.");
        }

        Optional<RecoveryRecord> existingRecordOpt = recoveryRecordRepository
                .findByPatientIdAndDayNumber(requestDto.getPatientId(), requestDto.getDayNumber());

        RecoveryRecord recordToSave;

        if (existingRecordOpt.isPresent()) {
            recordToSave = existingRecordOpt.get();
            recordToSave.setCompletedTaskIds(requestDto.getCompletedTaskIds());
            recordToSave.setDailyNotes(requestDto.getDailyNotes());
        } else {
            recordToSave = RecoveryRecord.builder()
                    .patientId(requestDto.getPatientId())
                    .dayNumber(requestDto.getDayNumber())
                    .completedTaskIds(requestDto.getCompletedTaskIds())
                    .dailyNotes(requestDto.getDailyNotes())
                    .build();
        }

        RecoveryRecord savedRecord = recoveryRecordRepository.save(recordToSave);
        return mapToResponseDto(savedRecord);
    }

    @Override
    public RecoveryRecordResponseDto getRecordByPatientAndDay(Long patientId, Integer dayNumber) {
        if (dayNumber < 1 || dayNumber > 42) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Day number must be between 1 and 42.");
        }

        RecoveryRecord record = recoveryRecordRepository
                .findByPatientIdAndDayNumber(patientId, dayNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Record not found for this day."));
        
        return mapToResponseDto(record);
    }

    @Override
    public List<RecoveryRecordResponseDto> getAllRecordsForPatient(Long patientId) {
        List<RecoveryRecord> records = recoveryRecordRepository.findByPatientIdOrderByDayNumberAsc(patientId);
        return records.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    private RecoveryRecordResponseDto mapToResponseDto(RecoveryRecord record) {
        return RecoveryRecordResponseDto.builder()
                .id(record.getId())
                .patientId(record.getPatientId())
                .dayNumber(record.getDayNumber())
                .completedTaskIds(record.getCompletedTaskIds())
                .dailyNotes(record.getDailyNotes())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .build();
    }
}
