package com.example.MathruAI_BackEnd.repository.checklist;

import com.example.MathruAI_BackEnd.entity.checklist.Checklist;
import com.example.MathruAI_BackEnd.entity.checklist.UserChecklistStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserChecklistStatusRepository extends JpaRepository<UserChecklistStatus, Long> {

    Optional<UserChecklistStatus> findByUserIdAndChecklist(Long userId, Checklist checklist);

    void deleteByChecklist(Checklist checklist);
}