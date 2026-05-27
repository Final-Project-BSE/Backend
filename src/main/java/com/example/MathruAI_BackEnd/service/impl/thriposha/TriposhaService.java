package com.example.MathruAI_BackEnd.service.impl.thriposha;

import com.example.MathruAI_BackEnd.dto.triposhaDto.TriposhaDTO;
import com.example.MathruAI_BackEnd.entity.triposha.TriposhaRecord;
import com.example.MathruAI_BackEnd.repository.thriposha.TriposhaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TriposhaService {

    private final TriposhaRepository repo;

    public TriposhaService(TriposhaRepository repo) {
        this.repo = repo;
    }

    public TriposhaRecord createRecord(TriposhaDTO dto) {
        TriposhaRecord record = new TriposhaRecord();
        record.setPatientId(dto.getPatientId());
        record.setMidwifeId(dto.getMidwifeId());
        record.setDistributionDate(dto.getDistributionDate());
        record.setQuantity(dto.getQuantity());
        record.setStatus(dto.getStatus());
        record.setNextDueDate(dto.getNextDueDate());
        record.setNotes(dto.getNotes());

        return repo.save(record);
    }



    public List<TriposhaRecord> getByMidwife(Long midwifeId) {
        return repo.findByMidwifeId(midwifeId);
    }

    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Record not found");
        }
        repo.deleteById(id);
    }

    public TriposhaRecord update(Long id, TriposhaDTO dto) {
        TriposhaRecord record = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        record.setQuantity(dto.getQuantity());
        record.setStatus(dto.getStatus());
        record.setNotes(dto.getNotes());
        record.setDistributionDate(dto.getDistributionDate());
        record.setNextDueDate(dto.getNextDueDate());

        return repo.save(record);
    }

    public List<TriposhaRecord> getByPatient(Long patientId) {
        return repo.findByPatientIdOrderByDistributionDateDesc(patientId);
    }
}