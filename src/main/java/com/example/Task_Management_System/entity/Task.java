package com.example.Task_Management_System.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate dueDate;
    private LocalDate completionDate;
    private boolean deleted = false;
    private LocalDateTime deletedAt;
    @Enumerated(EnumType.STRING)
    private Status status;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User owner;
    public enum Status {
        PENDING,
        COMPLETED,
        OVERDUE
    }
}
