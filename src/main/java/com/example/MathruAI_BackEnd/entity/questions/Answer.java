package com.example.MathruAI_BackEnd.entity.questions;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.w3c.dom.Text;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int answerId;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String answerText;
    private LocalDateTime createdAt;
}
