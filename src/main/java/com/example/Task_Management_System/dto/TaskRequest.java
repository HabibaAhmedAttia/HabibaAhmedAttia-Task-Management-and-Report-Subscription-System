package com.example.Task_Management_System.dto;

import com.example.Task_Management_System.entity.Task;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
public class TaskRequest {
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate dueDate;
    private LocalDate completionDate;
    private Task.Status status;
}
