package com.finance.finance_tracker.controller;

import com.finance.finance_tracker.dto.AuthRequest;
import com.finance.finance_tracker.dto.AuthResponse;
import com.finance.finance_tracker.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration and login")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register a new user")
    public void register(@Valid @RequestBody AuthRequest request) {
        authService.register(request);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Authenticate user and receive JWT")
    public AuthResponse login(@Valid @RequestBody AuthRequest request) {
        return authService.login(request);
    }
}