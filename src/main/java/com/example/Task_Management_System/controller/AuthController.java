package com.example.Task_Management_System.controller;
import com.example.Task_Management_System.dto.AuthResponse;
import com.example.Task_Management_System.dto.SignInRequest;
import com.example.Task_Management_System.dto.SignUpRequest;
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
            description = "Register a new user account with full name, email, and password. Returns the saved user object."
    )
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@Valid @RequestBody SignUpRequest request) {
        authService.signUp(request);
        return ResponseEntity.ok("User registered successfully");
    }
    @Operation(
            summary = "User sign-in",
            description = "Authenticate user with email and password, and return a JWT token on success."
    )
    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signIn(@Valid @RequestBody SignInRequest request) {
        //System.out.println(">>>> /signin called with email: " + request.getEmail());
        AuthResponse response = authService.signIn(request);
        return ResponseEntity.ok(response);
    }

}
