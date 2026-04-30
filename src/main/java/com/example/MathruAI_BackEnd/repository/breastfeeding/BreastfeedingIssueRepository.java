package com.example.MathruAI_BackEnd.repository.breastfeeding;

import com.example.MathruAI_BackEnd.entity.User;
import com.example.MathruAI_BackEnd.entity.breastfeeding.BreastfeedingIssue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BreastfeedingIssueRepository extends JpaRepository<BreastfeedingIssue, UUID> {

    List<BreastfeedingIssue> findByUserOrderByReportedAtDesc(User user);

    List<BreastfeedingIssue> findByUserAndResolved(User user, boolean resolved);

    Optional<BreastfeedingIssue> findByIdAndUser(UUID id, User user);
}