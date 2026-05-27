package com.example.MathruAI_BackEnd.repository.healthrecords;

import com.example.MathruAI_BackEnd.entity.healthrecords.RecordFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RecordFileRepository extends JpaRepository<RecordFile, UUID> {
}
