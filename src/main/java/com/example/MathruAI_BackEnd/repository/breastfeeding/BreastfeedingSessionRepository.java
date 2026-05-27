package com.example.MathruAI_BackEnd.repository.breastfeeding;

import com.example.MathruAI_BackEnd.entity.User;
import com.example.MathruAI_BackEnd.entity.breastfeeding.BreastfeedingSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BreastfeedingSessionRepository extends JpaRepository<BreastfeedingSession, UUID> {

    List<BreastfeedingSession> findByUserOrderByFeedingTimeDesc(User user);

    Optional<BreastfeedingSession> findByIdAndUser(UUID id, User user);
}