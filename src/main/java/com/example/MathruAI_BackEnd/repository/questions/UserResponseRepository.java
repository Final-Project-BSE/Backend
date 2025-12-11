package com.example.MathruAI_BackEnd.repository.questions;

import com.example.MathruAI_BackEnd.entity.questions.UserResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserResponseRepository extends JpaRepository<UserResponse, Integer> {
    List<UserResponse> findByUserIdAndFormId(int userId, int formId);
    boolean existsByUserIdAndFormId(int userId, int formId);
}