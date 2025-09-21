package com.example.MathruAI_BackEnd.entity.questions;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Options {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int optionId;
    private String optionText;
}
