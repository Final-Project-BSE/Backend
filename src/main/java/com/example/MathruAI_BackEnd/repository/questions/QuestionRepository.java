package com.example.MathruAI_BackEnd.repository.questions;

import com.example.MathruAI_BackEnd.entity.questions.Question;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question,Integer> {
}
