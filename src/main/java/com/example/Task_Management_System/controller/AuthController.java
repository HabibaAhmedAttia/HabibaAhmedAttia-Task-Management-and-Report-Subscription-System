package com.example.Task_Management_System.controller;
import com.example.Task_Management_System.dto.AuthResponse;
import com.example.Task_Management_System.dto.SignInRequest;
import com.example.Task_Management_System.dto.SignUpRequest;
import com.example.Task_Management_System.exception.ApiGenericResponse;
import com.example.Task_Management_System.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @Operation(
            summary = "User sign-up",
            description = "Register a new user account with full name, email, and password."
    )
    @PostMapping("/signup")
    public ResponseEntity<ApiGenericResponse<Object>> signUp(@Valid @RequestBody SignUpRequest request) {
        authService.signUp(request);
        return ResponseEntity.ok(ApiGenericResponse.success("User registered successfully", null));
    }
    @Operation(
            summary = "User sign-in",
            description = "Authenticate user with email and password, and return a JWT token on success."
    )
    @PostMapping("/signin")
    public ResponseEntity<ApiGenericResponse<AuthResponse>> signIn(@Valid @RequestBody SignInRequest request) {
        AuthResponse response = authService.signIn(request);
        return ResponseEntity.ok(ApiGenericResponse.success("User signed in successfully", response));
    }

}
