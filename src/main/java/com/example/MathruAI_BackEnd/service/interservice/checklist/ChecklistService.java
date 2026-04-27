package com.example.MathruAI_BackEnd.service.interservice.checklist;

import com.example.MathruAI_BackEnd.entity.checklist.Checklist;
import com.example.MathruAI_BackEnd.repository.checklist.ChecklistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChecklistService {

    private final ChecklistRepository repo;

    public List<Checklist> getAll() {
        return repo.findAll();
    }

    public Checklist create(Checklist item) {
        return repo.save(item);
    }

    public Checklist update(Long id, Checklist updated) {
        Checklist item = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        item.setName(updated.getName());
        item.setQuantity(updated.getQuantity());
        item.setCategory(updated.getCategory());
        item.setChecked(updated.isChecked());

        return repo.save(item);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}