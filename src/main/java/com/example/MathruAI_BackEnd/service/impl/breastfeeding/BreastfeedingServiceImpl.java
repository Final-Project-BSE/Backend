package com.example.MathruAI_BackEnd.service.impl.breastfeeding;

import com.example.MathruAI_BackEnd.dto.breastfeeding.BreastfeedingIssueRequestDto;
import com.example.MathruAI_BackEnd.dto.breastfeeding.BreastfeedingIssueResponseDto;
import com.example.MathruAI_BackEnd.dto.breastfeeding.BreastfeedingSessionRequestDto;
import com.example.MathruAI_BackEnd.dto.breastfeeding.BreastfeedingSessionResponseDto;
import com.example.MathruAI_BackEnd.dto.breastfeeding.BreastfeedingTipRequestDto;
import com.example.MathruAI_BackEnd.dto.breastfeeding.BreastfeedingTipResponseDto;
import com.example.MathruAI_BackEnd.entity.User;
import com.example.MathruAI_BackEnd.entity.breastfeeding.BreastfeedingIssue;
import com.example.MathruAI_BackEnd.entity.breastfeeding.BreastfeedingSession;
import com.example.MathruAI_BackEnd.entity.breastfeeding.BreastfeedingTip;
import com.example.MathruAI_BackEnd.repository.UserRepository;
import com.example.MathruAI_BackEnd.repository.breastfeeding.BreastfeedingIssueRepository;
import com.example.MathruAI_BackEnd.repository.breastfeeding.BreastfeedingSessionRepository;
import com.example.MathruAI_BackEnd.repository.breastfeeding.BreastfeedingTipRepository;
import com.example.MathruAI_BackEnd.service.interservice.breastfeeding.BreastfeedingServiceInter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BreastfeedingServiceImpl implements BreastfeedingServiceInter {

    private final BreastfeedingSessionRepository sessionRepository;
    private final BreastfeedingIssueRepository issueRepository;
    private final BreastfeedingTipRepository tipRepository;
    private final UserRepository userRepository;

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found."));
    }

    // ===================== SESSION =====================

    @Override
    public List<BreastfeedingSessionResponseDto> getAllSessions(String email) {
        User user = getUser(email);
        return sessionRepository.findByUserOrderByFeedingTimeDesc(user)
                .stream()
                .map(this::mapSessionToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BreastfeedingSessionResponseDto createSession(String email, BreastfeedingSessionRequestDto request) {
        User user = getUser(email);

        BreastfeedingSession session = BreastfeedingSession.builder()
                .user(user)
                .feedingTime(LocalDateTime.parse(request.getFeedingTime()))
                .side(BreastfeedingSession.FeedingSide.valueOf(request.getSide()))
                .durationMinutes(request.getDurationMinutes())
                .milkAmountMl(request.getMilkAmountMl())
                .notes(request.getNotes())
                .build();

        return mapSessionToResponse(sessionRepository.save(session));
    }

    @Override
    @Transactional
    public BreastfeedingSessionResponseDto updateSession(String email, UUID sessionId, BreastfeedingSessionRequestDto request) {
        User user = getUser(email);

        BreastfeedingSession session = sessionRepository.findByIdAndUser(sessionId, user)
                .orElseThrow(() -> new RuntimeException("Session not found."));

        if (request.getFeedingTime() != null) {
            session.setFeedingTime(LocalDateTime.parse(request.getFeedingTime()));
        }
        if (request.getSide() != null) {
            session.setSide(BreastfeedingSession.FeedingSide.valueOf(request.getSide()));
        }
        if (request.getDurationMinutes() != null) {
            session.setDurationMinutes(request.getDurationMinutes());
        }
        if (request.getMilkAmountMl() != null) {
            session.setMilkAmountMl(request.getMilkAmountMl());
        }
        if (request.getNotes() != null) {
            session.setNotes(request.getNotes());
        }

        return mapSessionToResponse(sessionRepository.save(session));
    }

    @Override
    @Transactional
    public void deleteSession(String email, UUID sessionId) {
        User user = getUser(email);

        BreastfeedingSession session = sessionRepository.findByIdAndUser(sessionId, user)
                .orElseThrow(() -> new RuntimeException("Session not found."));

        sessionRepository.delete(session);
    }

    // ===================== ISSUE =====================

    @Override
    public List<BreastfeedingIssueResponseDto> getAllIssues(String email) {
        User user = getUser(email);
        return issueRepository.findByUserOrderByReportedAtDesc(user)
                .stream()
                .map(this::mapIssueToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BreastfeedingIssueResponseDto> getUnresolvedIssues(String email) {
        User user = getUser(email);
        return issueRepository.findByUserAndResolved(user, false)
                .stream()
                .map(this::mapIssueToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BreastfeedingIssueResponseDto createIssue(String email, BreastfeedingIssueRequestDto request) {
        User user = getUser(email);

        BreastfeedingIssue issue = BreastfeedingIssue.builder()
                .user(user)
                .issueType(BreastfeedingIssue.IssueType.valueOf(request.getIssueType()))
                .description(request.getDescription())
                .severity(BreastfeedingIssue.SeverityLevel.valueOf(request.getSeverity()))
                .reportedAt(request.getReportedAt() != null
                        ? LocalDateTime.parse(request.getReportedAt())
                        : LocalDateTime.now())
                .resolved(request.isResolved())
                .midwifeNotes(request.getMidwifeNotes())
                .build();

        return mapIssueToResponse(issueRepository.save(issue));
    }

    @Override
    @Transactional
    public BreastfeedingIssueResponseDto updateIssue(String email, UUID issueId, BreastfeedingIssueRequestDto request) {
        User user = getUser(email);

        BreastfeedingIssue issue = issueRepository.findByIdAndUser(issueId, user)
                .orElseThrow(() -> new RuntimeException("Issue not found."));

        if (request.getIssueType() != null) {
            issue.setIssueType(BreastfeedingIssue.IssueType.valueOf(request.getIssueType()));
        }
        if (request.getDescription() != null) {
            issue.setDescription(request.getDescription());
        }
        if (request.getSeverity() != null) {
            issue.setSeverity(BreastfeedingIssue.SeverityLevel.valueOf(request.getSeverity()));
        }
        if (request.getReportedAt() != null) {
            issue.setReportedAt(LocalDateTime.parse(request.getReportedAt()));
        }
        if (request.getMidwifeNotes() != null) {
            issue.setMidwifeNotes(request.getMidwifeNotes());
        }

        issue.setResolved(request.isResolved());

        return mapIssueToResponse(issueRepository.save(issue));
    }

    @Override
    @Transactional
    public void deleteIssue(String email, UUID issueId) {
        User user = getUser(email);

        BreastfeedingIssue issue = issueRepository.findByIdAndUser(issueId, user)
                .orElseThrow(() -> new RuntimeException("Issue not found."));

        issueRepository.delete(issue);
    }

    // ===================== TIP =====================

    @Override
    public List<BreastfeedingTipResponseDto> getAllActiveTips() {
        return tipRepository.findByActiveTrue()
                .stream()
                .map(this::mapTipToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BreastfeedingTipResponseDto createTip(String email, BreastfeedingTipRequestDto request) {
        User user = getUser(email);

        BreastfeedingTip tip = BreastfeedingTip.builder()
                .createdBy(user)
                .category(BreastfeedingTip.TipCategory.valueOf(request.getCategory()))
                .title(request.getTitle())
                .content(request.getContent())
                .active(request.isActive())
                .build();

        return mapTipToResponse(tipRepository.save(tip));
    }

    @Override
    @Transactional
    public BreastfeedingTipResponseDto updateTip(String email, UUID tipId, BreastfeedingTipRequestDto request) {
        User user = getUser(email);

        BreastfeedingTip tip = tipRepository.findByIdAndCreatedBy(tipId, user)
                .orElseThrow(() -> new RuntimeException("Tip not found."));

        if (request.getCategory() != null) {
            tip.setCategory(BreastfeedingTip.TipCategory.valueOf(request.getCategory()));
        }
        if (request.getTitle() != null) {
            tip.setTitle(request.getTitle());
        }
        if (request.getContent() != null) {
            tip.setContent(request.getContent());
        }

        tip.setActive(request.isActive());

        return mapTipToResponse(tipRepository.save(tip));
    }

    @Override
    @Transactional
    public void deleteTip(String email, UUID tipId) {
        User user = getUser(email);

        BreastfeedingTip tip = tipRepository.findByIdAndCreatedBy(tipId, user)
                .orElseThrow(() -> new RuntimeException("Tip not found."));

        tipRepository.delete(tip);
    }

    // ===================== MAPPERS =====================

    private BreastfeedingSessionResponseDto mapSessionToResponse(BreastfeedingSession session) {
        return BreastfeedingSessionResponseDto.builder()
                .id(session.getId())
                .feedingTime(session.getFeedingTime().toString())
                .side(session.getSide().name())
                .durationMinutes(session.getDurationMinutes())
                .milkAmountMl(session.getMilkAmountMl())
                .notes(session.getNotes())
                .createdAt(session.getCreatedAt())
                .updatedAt(session.getUpdatedAt())
                .build();
    }

    private BreastfeedingIssueResponseDto mapIssueToResponse(BreastfeedingIssue issue) {
        return BreastfeedingIssueResponseDto.builder()
                .id(issue.getId())
                .issueType(issue.getIssueType().name())
                .description(issue.getDescription())
                .severity(issue.getSeverity().name())
                .reportedAt(issue.getReportedAt())
                .resolved(issue.isResolved())
                .midwifeNotes(issue.getMidwifeNotes())
                .createdAt(issue.getCreatedAt())
                .updatedAt(issue.getUpdatedAt())
                .build();
    }

    private BreastfeedingTipResponseDto mapTipToResponse(BreastfeedingTip tip) {
        return BreastfeedingTipResponseDto.builder()
                .id(tip.getId())
                .category(tip.getCategory().name())
                .title(tip.getTitle())
                .content(tip.getContent())
                .active(tip.isActive())
                .createdAt(tip.getCreatedAt())
                .updatedAt(tip.getUpdatedAt())
                .build();
    }
}