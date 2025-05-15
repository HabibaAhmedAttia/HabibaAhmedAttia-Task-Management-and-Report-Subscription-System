package com.example.Task_Management_System.controller;

import com.example.Task_Management_System.dto.TaskRequest;
import com.example.Task_Management_System.entity.Task;
import com.example.Task_Management_System.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @Operation(summary = "Create a new task", description = "Allows an authenticated user to create a new task by providing title, description, start and end dates, and status.")
    @PostMapping
    public ResponseEntity<Task> createTask(@Valid @RequestBody TaskRequest request, Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(taskService.createTask(request, email));
    }
    @Operation(summary = "Get tasks with optional filters", description = "Retrieves tasks for the authenticated user. You can optionally filter by status and/or a date range.")
    @GetMapping
    public ResponseEntity<List<Task>> getTasks(
            Authentication authentication,
            @RequestParam Optional<Task.Status> status,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> to) {

        String email = authentication.getName();
        return ResponseEntity.ok(taskService.getTasks(email, status, from, to));
    }
    @Operation(summary = "Update a task by ID", description = "Updates a task by its ID if it belongs to the authenticated user and is not deleted. All fields are updated from the request body.")
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable int id,
                                           @Valid @RequestBody TaskRequest request,
                                           Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(taskService.updateTask(id, request, email));
    }
    @Operation(summary = "Delete a task by ID", description = "Soft deletes a task by its ID. The task must belong to the authenticated user. Deleted tasks can be restored.")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable int id, Authentication authentication) {
        String email = authentication.getName();
        taskService.deleteTask(id, email);
        return ResponseEntity.ok("Task deleted successfully");
    }
    @Operation(summary = "Batch delete tasks in a date range", description = "Soft deletes all tasks for the authenticated user within the specified start and end date range.")
    @DeleteMapping("/batch")
    public ResponseEntity<String> deleteTasksInPeriod(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Authentication authentication) {

        String email = authentication.getName();
        taskService.deleteTasksInPeriod(from, to, email);
        return ResponseEntity.ok("Tasks in the period deleted");
    }
    @Operation(summary = "Restore the last deleted task", description = "Restores the most recently deleted task for the authenticated user. Useful for accidental deletions.")
    @PostMapping("/restore")
    public ResponseEntity<Task> restoreLastDeletedTask(Authentication authentication) {
        String email = authentication.getName();
        Task task = taskService.restoreLastDeletedTask(email);
        return ResponseEntity.ok(task);
    }
}
