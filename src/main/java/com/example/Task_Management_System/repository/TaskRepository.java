package com.example.Task_Management_System.repository;

import com.example.Task_Management_System.entity.Task;
import com.example.Task_Management_System.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Integer> {
    List<Task> findByOwnerAndDeletedFalse(User owner);
    List<Task> findByOwnerAndStatusAndDeletedFalse(User owner, Task.Status status);
    List<Task> findByOwnerAndStartDateBetweenAndDeletedFalse(User owner, LocalDate from, LocalDate to);
    List<Task> findByOwnerAndStatusAndStartDateBetweenAndDeletedFalse(User owner, Task.Status status, LocalDate from, LocalDate to);
    Optional<Task> findById(int id);
    Optional<Task> findTopByOwnerEmailAndDeletedTrueOrderByDeletedAtDesc(String email);
    List<Task> findByOwnerAndDueDateBetweenAndDeletedFalse(User user, LocalDate reportStartDate, LocalDate now);
}
