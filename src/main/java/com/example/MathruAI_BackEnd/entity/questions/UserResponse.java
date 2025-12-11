package com.example.MathruAI_BackEnd.entity.questions;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int responseId;
    private int userId;
    private int formId;
    private int questionId;
    @Lob
    @Column(columnDefinition = "TEXT")
    private String responseText;
    private LocalDateTime submittedAt;
}
