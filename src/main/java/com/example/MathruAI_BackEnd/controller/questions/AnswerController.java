package com.example.MathruAI_BackEnd.controller.questions;

import com.example.MathruAI_BackEnd.dto.questionDto.AnswerDto;
import com.example.MathruAI_BackEnd.entity.questions.Answer;
import com.example.MathruAI_BackEnd.service.impl.questions.AnswerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/answer")
public class AnswerController {

    private final AnswerServiceImpl answerServiceimpl;

    @Autowired
    public AnswerController(AnswerServiceImpl answerServiceimpl){
        this.answerServiceimpl = answerServiceimpl;
    }

    @GetMapping
    public ResponseEntity<List<AnswerDto>> getAllAnswers(){
        return ResponseEntity.ok(answerServiceimpl.getAllAnswers());
    }

    @PostMapping
    public ResponseEntity<AnswerDto> createAnswer(@RequestBody AnswerDto answerDto){
        return ResponseEntity.ok(answerServiceimpl.createAnswers(answerDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AnswerDto> updateAnswer(@PathVariable int id, @RequestBody AnswerDto answerDto){
        return ResponseEntity.ok(answerServiceimpl.updateAnswers(id,answerDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AnswerDto> deleteAnswer(@PathVariable int id){
        answerServiceimpl.deleteAnswers(id);
        return ResponseEntity.noContent().build();
    }

}
