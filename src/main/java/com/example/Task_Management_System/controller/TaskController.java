package com.example.Task_Management_System.controller;

import com.example.Task_Management_System.dto.PagedResponse;
import com.example.Task_Management_System.dto.TaskRequest;
import com.example.Task_Management_System.entity.Task;
import com.example.Task_Management_System.exception.ApiGenericResponse;
import com.example.Task_Management_System.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @Operation(summary = "Create a new task", description = "Allows an authenticated user to create a new task by providing at least title.")
    @PostMapping
    public ResponseEntity<ApiGenericResponse<Task>> createTask(@Validated(TaskRequest.OnCreate.class) @RequestBody TaskRequest request, Authentication authentication) {
        String email = authentication.getName();
        Task task = taskService.createTask(request, email);
        return ResponseEntity.ok(ApiGenericResponse.success("Task created successfully", task));
    }
    @Operation(summary = "Get tasks with optional filters", description = "Retrieves tasks for the authenticated user. You can optionally filter by status and/or a date range.")
    @GetMapping
    public ResponseEntity<ApiGenericResponse<PagedResponse<Task>>> getTasks(
            Authentication authentication,
            @RequestParam Optional<Task.Status> status,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> to,
            @RequestParam(defaultValue = "0") int page) {

        String email = authentication.getName();
        Page<Task> tasksPage = taskService.getTasksWithPagination(email, status, from, to, page);
        PagedResponse<Task> pagedResponse = new PagedResponse<>(tasksPage);
        String message = tasksPage.isEmpty() ? "No tasks found" : "Tasks retrieved successfully";
        return ResponseEntity.ok(ApiGenericResponse.success(message, pagedResponse));
    }
    @Operation(summary = "Update a task by ID", description = "Updates a task by its ID if it belongs to the authenticated user and is not deleted. All fields are updated from the request body.")
    @PutMapping("/{id}")
    public ResponseEntity<ApiGenericResponse<Task>> updateTask(@PathVariable int id,
                                           @Valid @RequestBody TaskRequest request,
                                           Authentication authentication) {
        String email = authentication.getName();
        Task task = taskService.updateTask(id, request, email);
        return ResponseEntity.ok(ApiGenericResponse.success("Task updated successfully", task));
    }
    @Operation(summary = "Delete a task by ID", description = "Soft deletes a task by its ID. The task must belong to the authenticated user. Deleted tasks can be restored.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiGenericResponse<Void>> deleteTask(@PathVariable int id, Authentication authentication) {
        String email = authentication.getName();
        taskService.deleteTask(id, email);
        return ResponseEntity.ok(ApiGenericResponse.success("Task deleted successfully", null));
    }
    @Operation(summary = "Batch delete tasks in a date range", description = "Soft deletes all tasks for the authenticated user within the specified start and end date range.")
    @DeleteMapping("/batch")
    public ResponseEntity<ApiGenericResponse<Void>> deleteTasksInPeriod(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Authentication authentication) {
        if (from == null || to == null) {
            return ResponseEntity.badRequest().body(
                    ApiGenericResponse.error("Please specify both 'from' and 'to' dates")
            );
        }
        if (from.isAfter(to)) {
            return ResponseEntity.badRequest().body(
                    ApiGenericResponse.error("'from' date must be before or equal to 'to' date")
            );
        }
        String email = authentication.getName();
        taskService.deleteTasksInPeriod(from, to, email);
        return ResponseEntity.ok(ApiGenericResponse.success("Tasks in the period deleted successfully", null));
    }
    @Operation(summary = "Restore the last deleted task", description = "Restores the most recently deleted task for the authenticated user. Useful for accidental deletions.")
    @PostMapping("/restore")
    public ResponseEntity<ApiGenericResponse<Task>> restoreLastDeletedTask(Authentication authentication) {
        String email = authentication.getName();
        Task task = taskService.restoreLastDeletedTask(email);
        return ResponseEntity.ok(ApiGenericResponse.success("Last deleted task restored successfully", task));
    }
}
