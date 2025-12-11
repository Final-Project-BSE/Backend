package com.example.MathruAI_BackEnd.repository.questions;

import com.example.MathruAI_BackEnd.entity.questions.PersonalDataForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonalDataFormRepository extends JpaRepository<PersonalDataForm, Integer> {
    List<PersonalDataForm> findByIsActiveTrue();
}