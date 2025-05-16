package com.example.Task_Management_System.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
@Getter
@Setter
@AllArgsConstructor
public class ApiGenericResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private Map<String, String> errors;

    public static <T> ApiGenericResponse<T> success(String message, T data) {
        return new ApiGenericResponse<>(true, message, data, null);
    }
    public static <T> ApiGenericResponse<T> error(String message) {
        return new ApiGenericResponse<>(false, message, null, null);
    }

    public static <T> ApiGenericResponse<T> error(String message, Map<String, String> errors) {
        return new ApiGenericResponse<>(false, message, null, errors);
    }
}
