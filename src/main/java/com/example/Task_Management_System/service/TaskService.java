package com.example.Task_Management_System.service;
import com.example.Task_Management_System.dto.TaskRequest;
import com.example.Task_Management_System.entity.Task;
import com.example.Task_Management_System.entity.User;
import com.example.Task_Management_System.repository.TaskRepository;
import com.example.Task_Management_System.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import static org.springframework.http.HttpStatus.*;
@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final SubscriptionService subscriptionService;

    public Task createTask(TaskRequest request, String userEmail) {
        if (request.getStartDate()==null)
        {
            request.setStartDate(LocalDate.now());
        }
        if (request.getDueDate()==null)
        {
            request.setDueDate(request.getStartDate().plusDays(3));
        }
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));
        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .startDate(request.getStartDate())
                .dueDate(request.getDueDate())
                .completionDate(request.getCompletionDate())
                .status(determineStatus(request))
                .owner(user)
                .build();
        return taskRepository.save(task);
    }
    public Page<Task> getTasksWithPagination(String userEmail, Optional<Task.Status> status, Optional<LocalDate> from, Optional<LocalDate> to, int page) {
    User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You are not authorized"));

    Pageable pageable = PageRequest.of(page, 5, Sort.by("startDate").descending());

    if (status.isPresent() && from.isPresent() && to.isPresent()) {
        return taskRepository.findByOwnerAndStatusAndStartDateBetweenAndDeletedFalse(
                user, status.get(), from.get(), to.get(), pageable);
    } else if (status.isPresent()) {
        return taskRepository.findByOwnerAndStatusAndDeletedFalse(user, status.get(), pageable);
    } else if (from.isPresent() && to.isPresent()) {
        return taskRepository.findByOwnerAndStartDateBetweenAndDeletedFalse(user, from.get(), to.get(), pageable);
    } else {
        return taskRepository.findByOwnerAndDeletedFalse(user, pageable);
    }
    }
    public List<Task> getTasksWithoutPagination(String userEmail, Optional<Task.Status> status, Optional<LocalDate> from, Optional<LocalDate> to) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));
        if (status.isPresent() && from.isPresent() && to.isPresent()) {
            return taskRepository.findByOwnerAndStatusAndStartDateBetweenAndDeletedFalse(
                    user, status.get(), from.get(), to.get());
        } else if (status.isPresent()) {
            return taskRepository.findByOwnerAndStatusAndDeletedFalse(user, status.get());
        } else if (from.isPresent() && to.isPresent()) {
            return taskRepository.findByOwnerAndStartDateBetweenAndDeletedFalse(user, from.get(), to.get());
        } else {
            return taskRepository.findByOwnerAndDeletedFalse(user);
        }
    }
    public Task updateTask(int id, TaskRequest request, String userEmail) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Task not found"));
        if (task.isDeleted()) {
            throw new ResponseStatusException(BAD_REQUEST, "restore task first");
        }
        if (!task.getOwner().getEmail().equals(userEmail)) {
            throw new ResponseStatusException(FORBIDDEN, "Not authorized to update this task");
        }
        if (request.getStartDate()==null)
        {
            request.setStartDate(LocalDate.now());
        }
        if (request.getDueDate()==null)
        {
            request.setDueDate(request.getStartDate().plusDays(3));
        }if (request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if (request.getStartDate() != null) {
            task.setStartDate(request.getStartDate());
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }
        if (request.getCompletionDate() != null) {
            task.setCompletionDate(request.getCompletionDate());
        }
        task.setStatus(determineStatus(request));
        return taskRepository.save(task);
    }
    public void deleteTask(int id, String userEmail) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Task not found"));
        if (!task.getOwner().getEmail().equals(userEmail)) {
            throw new ResponseStatusException(FORBIDDEN, "Not authorized to delete this task");
        }
        else if (task.isDeleted()) {
            throw new ResponseStatusException(BAD_REQUEST, "Task is already deleted");
        }
        task.setDeleted(true);
        task.setDeletedAt(LocalDateTime.now());
        taskRepository.save(task);
        subscriptionService.checkAndDeleteSubscriptionIfNoActiveTasks(userEmail);
    }
    public void deleteTasksInPeriod(LocalDate from, LocalDate to, String userEmail) {
        List<Task> tasks = getTasksWithoutPagination(userEmail, Optional.empty(), Optional.of(from), Optional.of(to));
        if (tasks.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "No Tasks found in this period");
        }
        for (Task task : tasks) {
            task.setDeleted(true);
            task.setDeletedAt(LocalDateTime.now());
        }
        taskRepository.saveAll(tasks);
    }
    public Task restoreLastDeletedTask(String userEmail) {
        Task task = taskRepository.findTopByOwnerEmailAndDeletedTrueOrderByDeletedAtDesc(userEmail)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "No deleted task found"));
        task.setDeleted(false);
        task.setDeletedAt(null);
        return taskRepository.save(task);
    }
    private Task.Status determineStatus(TaskRequest request) {
        if (request.getCompletionDate() != null) {
            return Task.Status.COMPLETED;
        }
        if (LocalDate.now().isAfter(request.getDueDate())) {
            return Task.Status.OVERDUE;
        }
        return Task.Status.PENDING;
    }
}
