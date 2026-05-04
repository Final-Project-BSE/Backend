package com.example.MathruAI_BackEnd.repository.breastfeeding;

import com.example.MathruAI_BackEnd.entity.User;
import com.example.MathruAI_BackEnd.entity.breastfeeding.BreastfeedingTip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BreastfeedingTipRepository extends JpaRepository<BreastfeedingTip, UUID> {

    List<BreastfeedingTip> findByActiveTrue();

    List<BreastfeedingTip> findByCreatedBy(User createdBy);

    List<BreastfeedingTip> findByCategory(BreastfeedingTip.TipCategory category);

    Optional<BreastfeedingTip> findByIdAndCreatedBy(UUID id, User createdBy);
}