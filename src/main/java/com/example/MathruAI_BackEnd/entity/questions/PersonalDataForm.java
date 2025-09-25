package com.example.MathruAI_BackEnd.entity.questions;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonalDataForm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int formId;
    private String formName;
    private String description;
    private boolean isActive;

    @OneToMany(mappedBy = "form", cascade = CascadeType.ALL)
    private List<Question> questions;
}