package com.example.MathruAI_BackEnd.service.Profile;

import com.example.MathruAI_BackEnd.dto.ProfileDto.ChangeEmailRequestDto;
import com.example.MathruAI_BackEnd.dto.ProfileDto.ChangePasswordRequestDto;
import com.example.MathruAI_BackEnd.dto.ProfileDto.ChangeRoleRequestDto;
import com.example.MathruAI_BackEnd.dto.ProfileDto.ProfileResponseDto;
import com.example.MathruAI_BackEnd.dto.ProfileDto.ProfileUpdateRequestDto;
import com.example.MathruAI_BackEnd.entity.User;
import com.example.MathruAI_BackEnd.repository.UserRepository;
import com.example.MathruAI_BackEnd.repository.reproductive.fertilitytracker.CycleDataRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CycleDataRepository cycleDataRepository;

    @Value("${file.upload-dir2:uploads/profile-images}")
    private String uploadDir;

    public ProfileResponseDto getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return mapToResponseDto(user);
    }

    public ProfileResponseDto updateProfile(Long userId, ProfileUpdateRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getDateOfBirth() != null) user.setDateOfBirth(request.getDateOfBirth());

        if (request.getNationalIdNumber() != null &&
                !request.getNationalIdNumber().equals(user.getNationalIdNumber()) &&
                userRepository.existsByNationalIdNumber(request.getNationalIdNumber())) {
            throw new RuntimeException("National ID number is already in use.");
        }

        if (request.getNationalIdNumber() != null) user.setNationalIdNumber(request.getNationalIdNumber());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getProfileImageUrl() != null) user.setProfileImageUrl(request.getProfileImageUrl());
        if (request.getArea() != null) user.setArea(request.getArea());
        if (request.getDistrict() != null) user.setDistrict(normalizeText(request.getDistrict()));
        if (request.getMohArea() != null) user.setMohArea(normalizeText(request.getMohArea()));
        if (request.getLatitude() != null) user.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) user.setLongitude(request.getLongitude());

        return mapToResponseDto(userRepository.save(user));
    }

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

    public ProfileResponseDto changeRole(Long userId, ChangeRoleRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        if (request.getRoles() == null || request.getRoles().isEmpty()) {
            throw new RuntimeException("Roles cannot be empty.");
        }

        user.setRoles(request.getRoles());
        return mapToResponseDto(userRepository.save(user));
    }

    public ProfileResponseDto uploadProfileImage(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Profile image file is empty.");
        }

        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = "";

            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            String fileName = UUID.randomUUID() + extension;
            Path filePath = uploadPath.resolve(fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            user.setProfileImageUrl("/uploads/profile-images/" + fileName);
            return mapToResponseDto(userRepository.save(user));

        } catch (IOException e) {
            throw new RuntimeException("Failed to upload profile image.", e);
        }
    }

    @Transactional
    public void deleteAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        cycleDataRepository.deleteByUserId(userId);
        userRepository.delete(user);
    }

    private ProfileResponseDto mapToResponseDto(User user) {
        return ProfileResponseDto.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .dateOfBirth(user.getDateOfBirth())
                .nationalIdNumber(user.getNationalIdNumber())
                .address(user.getAddress())
                .profileImageUrl(user.getProfileImageUrl())
                .area(user.getArea())
                .district(user.getDistrict())
                .mohArea(user.getMohArea())
                .latitude(user.getLatitude())
                .longitude(user.getLongitude())
                .assignedMidwifeId(user.getAssignedMidwife() != null ? user.getAssignedMidwife().getId() : null)
                .roles(user.getRoles())
                .build();
    }

    private String normalizeText(String value) {
        return value == null ? null : value.trim();
    }
}