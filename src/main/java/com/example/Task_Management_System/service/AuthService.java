package com.example.Task_Management_System.service;

import com.example.Task_Management_System.dto.AuthResponse;
import com.example.Task_Management_System.dto.SignInRequest;
import com.example.Task_Management_System.dto.SignUpRequest;
import com.example.Task_Management_System.entity.User;
import com.example.Task_Management_System.repository.UserRepository;
import com.example.Task_Management_System.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    public void signUp(SignUpRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email is already in use");
        }
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        userRepository.save(user);
    }
    public AuthResponse signIn(SignInRequest request) {
//        System.out.println(">>> signIn() called with: " + request.getEmail());
//        System.out.println(">>> Raw password: " + request.getPassword());
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
//        System.out.println(">>> Encoded password in DB: " + user.getPassword());
//        System.out.println(">>> Password match: " + passwordEncoder.matches(request.getPassword(), user.getPassword()));


        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }
        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token);
    }
}
