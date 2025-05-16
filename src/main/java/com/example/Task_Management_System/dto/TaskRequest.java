package com.example.Task_Management_System.dto;

import com.example.Task_Management_System.entity.Task;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
public class TaskRequest {
    @NotBlank(message = "Title of task is required")
    private String title;
    //@NotBlank(message = "Description of task is required")
    private String description;
    //@NotNull(message = "StartDate of task is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startDate;
    //@NotNull(message = "Deadline of task is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dueDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate completionDate;
    private Task.Status status= Task.Status.PENDING;
    @AssertTrue(message = "Start date must be before or equal to due date")
    public boolean isValidDateRange() {
        if (startDate == null || dueDate == null) {
            return true;
        }
        return !startDate.isAfter(dueDate);
    }
    @AssertTrue(message = "Completion date must be after or equal to start date")
    public boolean isValidCompletionDate() {
        if (completionDate == null || startDate == null) {
            return true;
        }
        return !completionDate.isBefore(startDate);
    }
}
