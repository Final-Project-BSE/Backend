package com.example.MathruAI_BackEnd.controller.Profile;

import com.example.MathruAI_BackEnd.dto.ProfileDto.ChangeEmailRequestDto;
import com.example.MathruAI_BackEnd.dto.ProfileDto.ChangePasswordRequestDto;
import com.example.MathruAI_BackEnd.dto.ProfileDto.ChangeRoleRequestDto;
import com.example.MathruAI_BackEnd.dto.ProfileDto.ProfileResponseDto;
import com.example.MathruAI_BackEnd.dto.ProfileDto.ProfileUpdateRequestDto;
import com.example.MathruAI_BackEnd.service.Profile.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/{id}")
    public ResponseEntity<ProfileResponseDto> getProfile(@PathVariable Long id) {
        return ResponseEntity.ok(profileService.getProfile(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfileResponseDto> updateProfile(
            @PathVariable Long id,
            @RequestBody ProfileUpdateRequestDto request) {
        return ResponseEntity.ok(profileService.updateProfile(id, request));
    }

    @PatchMapping("/{id}/change-password")
    public ResponseEntity<String> changePassword(
            @PathVariable Long id,
            @RequestBody ChangePasswordRequestDto request) {
        return ResponseEntity.ok(profileService.changePassword(id, request));
    }

    @PatchMapping("/{id}/change-email")
    public ResponseEntity<ProfileResponseDto> changeEmail(
            @PathVariable Long id,
            @RequestBody ChangeEmailRequestDto request) {
        return ResponseEntity.ok(profileService.changeEmail(id, request));
    }

    @PatchMapping("/{id}/change-role")
    public ResponseEntity<ProfileResponseDto> changeRole(
            @PathVariable Long id,
            @RequestBody ChangeRoleRequestDto request) {
        return ResponseEntity.ok(profileService.changeRole(id, request));
    }

    @PostMapping(value = "/{id}/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProfileResponseDto> uploadProfileImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(profileService.uploadProfileImage(id, file));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAccount(@PathVariable Long id) {
        profileService.deleteAccount(id);
        return ResponseEntity.ok("Account permanently deleted.");
    }
}