package com.example.Task_Management_System.exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiGenericResponse<Object>> handleAuthenticationException(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiGenericResponse.error("You are not authorized"));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiGenericResponse<Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        ApiGenericResponse<Object> response = ApiGenericResponse.error("Validation failed", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiGenericResponse<Object>> handleResponseStatusException(ResponseStatusException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", ex.getReason());
        ApiGenericResponse<Object> response = ApiGenericResponse.error(ex.getReason(),errors);
        return new ResponseEntity<>(response, ex.getStatusCode());
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiGenericResponse<Object>> handleInvalidFormat(HttpMessageNotReadableException ex) {
        String msg = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
        if (msg != null && msg.contains("Frequency")) {
            return ResponseEntity.badRequest()
                    .body(ApiGenericResponse.error("Invalid frequency value. Allowed values are: DAILY, WEEKLY, MONTHLY."));
        }
        if (msg != null && msg.contains("Date")) {
            return ResponseEntity.badRequest()
                    .body(ApiGenericResponse.error("Invalid date format. Please use yyyy-MM-dd."));
        }
        return ResponseEntity.badRequest()
                .body(ApiGenericResponse.error("Invalid input format."));
    }
}
