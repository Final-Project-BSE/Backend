package com.example.MathruAI_BackEnd.service.impl.user;

import com.example.MathruAI_BackEnd.dto.userDto.UserResponseDto;
import com.example.MathruAI_BackEnd.dto.userDto.UserUpdateRequest;
import com.example.MathruAI_BackEnd.entity.User;
import com.example.MathruAI_BackEnd.repository.UserRepository;
import com.example.MathruAI_BackEnd.service.interservice.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private UserResponseDto toDto(User u) {
        return new UserResponseDto(
                u.getId(),
                u.getFirstName(),
                u.getLastName(),
                u.getEmail(),
                u.getPhoneNumber(),
                u.getDateOfBirth(),
                u.getRoles()
        );
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public UserResponseDto getUserById(Long id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return toDto(u);
    }

    @Override
    public UserResponseDto updateUser(Long id, UserUpdateRequest request) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (request.getFirstName() != null) u.setFirstName(request.getFirstName());
        if (request.getLastName() != null) u.setLastName(request.getLastName());
        if (request.getPhoneNumber() != null) u.setPhoneNumber(request.getPhoneNumber());
        if (request.getDateOfBirth() != null) u.setDateOfBirth(request.getDateOfBirth());
        if (request.getRoles() != null) u.setRoles(request.getRoles());

        return toDto(userRepository.save(u));
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        userRepository.deleteById(id);
    }

    public UserResponseDto getByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));

        return new UserResponseDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getDateOfBirth(),
                user.getRoles()
        );
    }
}
