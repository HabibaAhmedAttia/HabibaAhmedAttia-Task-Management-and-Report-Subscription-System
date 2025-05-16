package com.example.Task_Management_System.exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
@RestControllerAdvice
public class GlobalExceptionHandler {
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
        ApiGenericResponse<Object> response = ApiGenericResponse.error(ex.getReason());
        return new ResponseEntity<>(response, ex.getStatusCode());
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiGenericResponse<Object>> handleInvalidDateFormat(HttpMessageNotReadableException ex) {
        ApiGenericResponse<Object> response = ApiGenericResponse.error("Invalid date format. Please use yyyy-MM-dd.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
