package com.example.MathruAI_BackEnd.repository.checklist;

import com.example.MathruAI_BackEnd.entity.checklist.Checklist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChecklistRepository extends JpaRepository<Checklist, Long> {
}