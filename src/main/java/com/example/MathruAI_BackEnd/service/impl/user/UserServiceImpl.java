package com.example.MathruAI_BackEnd.service.impl.user;

import com.example.MathruAI_BackEnd.dto.userDto.UserResponseDto;
import com.example.MathruAI_BackEnd.dto.userDto.UserUpdateRequest;
import com.example.MathruAI_BackEnd.entity.User;
import com.example.MathruAI_BackEnd.repository.UserRepository;
import com.example.MathruAI_BackEnd.service.interservice.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapUser)
                .toList();
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        return mapUser(getUserOrThrow(id));
    }

    @Override
    public UserResponseDto updateUser(Long id, UserUpdateRequest request) {
        User user = getUserOrThrow(id);

        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null) user.setLastName(request.getLastName());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getDateOfBirth() != null) user.setDateOfBirth(request.getDateOfBirth());
        if (request.getNationalIdNumber() != null) user.setNationalIdNumber(request.getNationalIdNumber());
        if (request.getAddress() != null) user.setAddress(request.getAddress());
        if (request.getProfileImageUrl() != null) user.setProfileImageUrl(request.getProfileImageUrl());
        if (request.getArea() != null) user.setArea(request.getArea());
        if (request.getDistrict() != null) user.setDistrict(request.getDistrict().trim());
        if (request.getMohArea() != null) user.setMohArea(request.getMohArea().trim());
        if (request.getLatitude() != null) user.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) user.setLongitude(request.getLongitude());
        if (request.getRoles() != null && !request.getRoles().isEmpty()) user.setRoles(request.getRoles());

        return mapUser(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserResponseDto getByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(this::mapUser)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    private UserResponseDto mapUser(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getDateOfBirth(),
                user.getNationalIdNumber(),
                user.getAddress(),
                user.getProfileImageUrl(),
                user.getArea(),
                user.getDistrict(),
                user.getMohArea(),
                user.getLatitude(),
                user.getLongitude(),
                user.getAssignedMidwife() != null ? user.getAssignedMidwife().getId() : null,
                user.getRoles()
        );
    }
}