package com.example.MathruAI_BackEnd.controller.thriposha;

import com.example.MathruAI_BackEnd.dto.triposhaDto.TriposhaDTO;
import com.example.MathruAI_BackEnd.entity.triposha.TriposhaRecord;
import com.example.MathruAI_BackEnd.service.impl.thriposha.TriposhaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/triposha")
@CrossOrigin
public class TriposhaController {

    private final TriposhaService service;

    public TriposhaController(TriposhaService service) {
        this.service = service;
    }

    @PostMapping
    public TriposhaRecord create(@RequestBody TriposhaDTO dto) {
        return service.createRecord(dto);
    }

    @GetMapping("/midwife/{midwifeId}")
    public List<TriposhaRecord> getByMidwife(@PathVariable Long midwifeId) {
        return service.getByMidwife(midwifeId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok("Deleted successfully");
    }

    @PutMapping("/{id}")
    public TriposhaRecord update(@PathVariable Long id, @RequestBody TriposhaDTO dto) {
        return service.update(id, dto);
    }

    @GetMapping("/patient/{patientId}")
    public List<TriposhaRecord> getByPatient(@PathVariable Long patientId) {
        return service.getByPatient(patientId);
    }
}