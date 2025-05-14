package com.example.Task_Management_System.controller;
import com.example.Task_Management_System.dto.AuthResponse;
import com.example.Task_Management_System.dto.SignInRequest;
import com.example.Task_Management_System.dto.SignUpRequest;
import com.example.Task_Management_System.service.AuthService;
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

    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@Valid @RequestBody SignUpRequest request) {
        authService.signUp(request);
        return ResponseEntity.ok("User registered successfully");
    }
    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> signIn(@Valid @RequestBody SignInRequest request) {
        //System.out.println(">>>> /signin called with email: " + request.getEmail());
        AuthResponse response = authService.signIn(request);
        return ResponseEntity.ok(response);
    }
}
