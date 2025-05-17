package com.example.Task_Management_System.service;

import com.example.Task_Management_System.dto.SubscriptionRequest;
import com.example.Task_Management_System.entity.Subscription;
import com.example.Task_Management_System.entity.Task;
import com.example.Task_Management_System.entity.User;
import com.example.Task_Management_System.repository.SubscriptionRepository;
import com.example.Task_Management_System.repository.TaskRepository;
import com.example.Task_Management_System.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalTime;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    public Subscription subscribe(SubscriptionRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));
        List<Task> userTasks = taskRepository.findByOwnerAndDeletedFalse(user);
        if (userTasks.isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "User has no tasks, subscription not allowed");
        }
        subscriptionRepository.findByUser(user).ifPresent(existingSubscription -> {
            throw new ResponseStatusException(BAD_REQUEST,
                    "User already has an active subscription");
        });
        LocalTime reportTime = LocalTime.of(request.getReportHour(), 0);
        Subscription subscription = Subscription.builder()
                .user(user)
                .frequency(request.getFrequency())
                .startDate(request.getStartDate())
                .reportHour(reportTime)
                .build();

        return subscriptionRepository.save(subscription);
    }

    public void unsubscribe(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));
        Subscription subscription = subscriptionRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Subscription not found"));
        subscriptionRepository.delete(subscription);
    }
    public void checkAndDeleteSubscriptionIfNoActiveTasks(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "User not found"));

        List<Task> activeTasks = taskRepository.findByOwnerAndDeletedFalse(user);
        if (activeTasks.isEmpty()) {
            subscriptionRepository.findByUser(user).ifPresent(subscriptionRepository::delete);
        }
    }

}
