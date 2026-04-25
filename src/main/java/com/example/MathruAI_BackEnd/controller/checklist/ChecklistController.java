package com.example.MathruAI_BackEnd.controller.checklist;

import com.example.MathruAI_BackEnd.entity.checklist.Checklist;
import com.example.MathruAI_BackEnd.service.interservice.checklist.ChecklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checklist")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ChecklistController {

    private final ChecklistService service;

    @GetMapping
    public List<Checklist> getAll() {
        return service.getAll();
    }

    @PostMapping
    public Checklist create(@RequestBody Checklist item) {
        return service.create(item);
    }

    @PutMapping("/{id}")
    public Checklist update(@PathVariable Long id, @RequestBody Checklist item) {
        return service.update(id, item);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}