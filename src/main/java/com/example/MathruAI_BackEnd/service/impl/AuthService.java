package com.example.MathruAI_BackEnd.service.impl;

import com.example.MathruAI_BackEnd.dto.AuthResponse;
import com.example.MathruAI_BackEnd.dto.LoginRequest;
import com.example.MathruAI_BackEnd.dto.SignupRequest;
import com.example.MathruAI_BackEnd.entity.User;
import com.example.MathruAI_BackEnd.repository.UserRepository;
import com.example.MathruAI_BackEnd.security.JwtUtil;
import com.example.MathruAI_BackEnd.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.MathruAI_BackEnd.entity.*;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    public ResponseEntity<?> signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "FAIL");
            response.put("message", "Email is already taken!");
            response.put("data", null);
            return ResponseEntity.badRequest().body(response);
        }

        // Create new user's account
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setDateOfBirth(request.getDateOfBirth());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRoles(request.getRoles() != null ? request.getRoles() : Set.of(Role.HOPE_TO_PREGNANT_MOTHER));

        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("message", "User registered successfully!");
        response.put("data", null);
        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity<?> signin(LoginRequest request) {
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(),
                            request.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtil.generateJwtToken(authentication);

            UserPrincipal userDetails = (UserPrincipal) authentication.getPrincipal();
            User user = userRepository.findByEmail(userDetails.getEmail()).get();

            // Convert roles to string array
            Set<String> roleStrings = user.getRoles().stream()
                    .map(Enum::name)
                    .collect(Collectors.toSet());

            // Create data object
            Map<String, Object> data = new HashMap<>();
            data.put("email", user.getEmail());
            data.put("token", jwt);
            data.put("roles", roleStrings);

            // Create response wrapper
            Map<String, Object> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "Login successful");
            response.put("data", data);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "FAIL");
            response.put("message", "The email or password you entered is incorrect. Please try again.");
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    public ResponseEntity<?> validateToken(HttpServletRequest request) {
        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtil.validateJwtToken(jwt)) {
                String email = jwtUtil.getUserNameFromJwtToken(jwt);
                Optional<User> user = userRepository.findByEmail(email);

                if (user.isPresent()) {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "SUCCESS");
                    response.put("message", "Token is valid");
                    response.put("data", null);
                    return ResponseEntity.ok().body(response);
                }
            }
            Map<String, Object> response = new HashMap<>();
            response.put("status", "FAIL");
            response.put("message", "Invalid token");
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "FAIL");
            response.put("message", "Token validation failed");
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }

    public User updateUser(Long id, User user) {
        User existingUser = getUserById(id);
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setEmail(user.getEmail());
        return userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}