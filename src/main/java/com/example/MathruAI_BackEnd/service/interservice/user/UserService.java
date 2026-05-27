package com.example.MathruAI_BackEnd.service.interservice.user;

import com.example.MathruAI_BackEnd.dto.userDto.UserResponseDto;
import com.example.MathruAI_BackEnd.dto.userDto.UserUpdateRequest;

import java.util.List;

public interface UserService {
    List<UserResponseDto> getAllUsers();
    UserResponseDto getUserById(Long id);
    UserResponseDto updateUser(Long id, UserUpdateRequest request);
    void deleteUser(Long id);
    UserResponseDto getByEmail(String email);
}