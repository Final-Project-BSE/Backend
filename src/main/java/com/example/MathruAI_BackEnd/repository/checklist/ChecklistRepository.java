package com.example.MathruAI_BackEnd.repository.checklist;

import com.example.MathruAI_BackEnd.entity.checklist.Checklist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChecklistRepository extends JpaRepository<Checklist, Long> {

    List<Checklist> findByMidwifeId(Long midwifeId);
}