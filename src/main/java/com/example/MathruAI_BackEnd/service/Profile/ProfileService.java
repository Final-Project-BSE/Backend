package com.example.MathruAI_BackEnd.service.Profile;

import com.example.MathruAI_BackEnd.dto.ProfileDto.ChangeEmailRequestDto;
import com.example.MathruAI_BackEnd.dto.ProfileDto.ChangePasswordRequestDto;
import com.example.MathruAI_BackEnd.dto.ProfileDto.ChangeRoleRequestDto;
import com.example.MathruAI_BackEnd.dto.ProfileDto.ProfileResponseDto;
import com.example.MathruAI_BackEnd.dto.ProfileDto.ProfileUpdateRequestDto;
import com.example.MathruAI_BackEnd.entity.User;
import com.example.MathruAI_BackEnd.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ─── View Profile ────────────────────────────────────────────────────────────
    public ProfileResponseDto getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return mapToResponseDto(user);
    }

    // ─── Update Basic Profile (firstName, lastName, phone, dob) ─────────────────
    public ProfileResponseDto updateProfile(Long userId, ProfileUpdateRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getDateOfBirth() != null) user.setDateOfBirth(request.getDateOfBirth());

        return mapToResponseDto(userRepository.save(user));
    }

    // ─── Change Password ─────────────────────────────────────────────────────────
    public String changePassword(Long userId, ChangePasswordRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect.");
        }
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new RuntimeException("New passwords do not match.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return "Password changed successfully.";
    }

    // ─── Change Email ─────────────────────────────────────────────────────────────
    public ProfileResponseDto changeEmail(Long userId, ChangeEmailRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Password is incorrect.");
        }
        if (userRepository.existsByEmail(request.getNewEmail())) {
            throw new RuntimeException("Email is already in use.");
        }

        user.setEmail(request.getNewEmail());
        return mapToResponseDto(userRepository.save(user));
    }

    // ─── Change Role ──────────────────────────────────────────────────────────────
    public ProfileResponseDto changeRole(Long userId, ChangeRoleRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        if (request.getRoles() == null || request.getRoles().isEmpty()) {
            throw new RuntimeException("Roles cannot be empty.");
        }

        user.setRoles(request.getRoles());
        return mapToResponseDto(userRepository.save(user));
    }

    // ─── Permanent Delete ─────────────────────────────────────────────────────────
    public void deleteAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        userRepository.delete(user);
    }

    // ─── Mapper Helper ────────────────────────────────────────────────────────────
    private ProfileResponseDto mapToResponseDto(User user) {
        return ProfileResponseDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .dateOfBirth(user.getDateOfBirth())
                .roles(user.getRoles())
                .build();
    }
}