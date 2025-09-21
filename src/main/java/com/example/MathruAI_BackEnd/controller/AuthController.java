package com.example.MathruAI_BackEnd.controller;

import com.example.MathruAI_BackEnd.dto.LoginRequest;
import com.example.MathruAI_BackEnd.dto.SignupRequest;
import com.example.MathruAI_BackEnd.entity.User;
import com.example.MathruAI_BackEnd.service.impl.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User authentication and management")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    @Operation(summary = "User signup", description = "Register a new user")
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {
        return ResponseEntity.ok(authService.signup(request));
    }

    @PostMapping("/signin")
    @Operation(summary = "User signin", description = "Login with email and password")
    public ResponseEntity<?> signin(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.signin(request));
    }

    @GetMapping("/validate-token")
    @Operation(summary = "Validate token", description = "Check if the authentication token is valid")
    public ResponseEntity<?> validateToken(HttpServletRequest request) {
        return authService.validateToken(request);
    }

    @GetMapping("/users/get/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve user details by ID")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(authService.getUserById(id));
    }

    @PutMapping("/users/update/{id}")
    @Operation(summary = "Update user", description = "Update user information")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        return ResponseEntity.ok(authService.updateUser(id, user));
    }

    @DeleteMapping("/users/delete/{id}")
    @Operation(summary = "Delete user", description = "Delete user from the system")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        authService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}