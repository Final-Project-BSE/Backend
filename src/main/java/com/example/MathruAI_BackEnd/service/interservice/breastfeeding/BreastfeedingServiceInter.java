package com.example.MathruAI_BackEnd.service.interservice.breastfeeding;

import com.example.MathruAI_BackEnd.dto.breastfeeding.BreastfeedingIssueRequestDto;
import com.example.MathruAI_BackEnd.dto.breastfeeding.BreastfeedingIssueResponseDto;
import com.example.MathruAI_BackEnd.dto.breastfeeding.BreastfeedingSessionRequestDto;
import com.example.MathruAI_BackEnd.dto.breastfeeding.BreastfeedingSessionResponseDto;
import com.example.MathruAI_BackEnd.dto.breastfeeding.BreastfeedingTipRequestDto;
import com.example.MathruAI_BackEnd.dto.breastfeeding.BreastfeedingTipResponseDto;

import java.util.List;
import java.util.UUID;

public interface BreastfeedingServiceInter {

    // ===================== SESSION =====================
    List<BreastfeedingSessionResponseDto> getAllSessions(String email);
    BreastfeedingSessionResponseDto createSession(String email, BreastfeedingSessionRequestDto request);
    BreastfeedingSessionResponseDto updateSession(String email, UUID sessionId, BreastfeedingSessionRequestDto request);
    void deleteSession(String email, UUID sessionId);

    // ===================== ISSUE =====================
    List<BreastfeedingIssueResponseDto> getAllIssues(String email);
    List<BreastfeedingIssueResponseDto> getUnresolvedIssues(String email);
    BreastfeedingIssueResponseDto createIssue(String email, BreastfeedingIssueRequestDto request);
    BreastfeedingIssueResponseDto updateIssue(String email, UUID issueId, BreastfeedingIssueRequestDto request);
    void deleteIssue(String email, UUID issueId);

    // ===================== TIP =====================
    List<BreastfeedingTipResponseDto> getAllActiveTips();
    BreastfeedingTipResponseDto createTip(String email, BreastfeedingTipRequestDto request);
    BreastfeedingTipResponseDto updateTip(String email, UUID tipId, BreastfeedingTipRequestDto request);
    void deleteTip(String email, UUID tipId);

    // ===================== MIDWIFE =====================
    List<BreastfeedingSessionResponseDto> getPatientSessionsForMidwife(Long midwifeId, Long patientId);
    List<BreastfeedingIssueResponseDto> getPatientIssuesForMidwife(Long midwifeId, Long patientId);
    BreastfeedingIssueResponseDto updatePatientIssueForMidwife(Long midwifeId, Long patientId, UUID issueId, BreastfeedingIssueRequestDto request);
}