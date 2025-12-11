package com.example.MathruAI_BackEnd.controller.questions;

import com.example.MathruAI_BackEnd.dto.questionDto.OptionDto;
import com.example.MathruAI_BackEnd.service.impl.questions.OptionsServiceImpl;
import org.aspectj.weaver.ResolvedPointcutDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.function.RenderingResponse;

import java.util.List;

@RestController
@RequestMapping("/api/option")
public class OptionController {

    private final OptionsServiceImpl optionsService;

    @Autowired
    public OptionController(OptionsServiceImpl optionsService){
        this.optionsService = optionsService;
    }

    @GetMapping
    public ResponseEntity<List<OptionDto>> getAllOption(){
        return ResponseEntity.ok(optionsService.getAllOptions());
    }

    @PostMapping
    public ResponseEntity<OptionDto> createOption(@RequestBody OptionDto optionDto){
        return ResponseEntity.ok(optionsService.createOptions(optionDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OptionDto> updateOption(@PathVariable int id, @RequestBody OptionDto optionDto){
        return ResponseEntity.ok(optionsService.updateOptions(id, optionDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OptionDto> deleteOption(@PathVariable int id){
        optionsService.deleteOptions(id);
        return ResponseEntity.noContent().build();
    }
}
