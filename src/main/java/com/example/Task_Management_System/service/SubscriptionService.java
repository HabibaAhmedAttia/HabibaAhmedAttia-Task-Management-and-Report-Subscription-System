package com.example.Task_Management_System.service;

import com.example.Task_Management_System.dto.SubscriptionRequest;
import com.example.Task_Management_System.entity.Subscription;
import com.example.Task_Management_System.entity.User;
import com.example.Task_Management_System.repository.SubscriptionRepository;
import com.example.Task_Management_System.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.time.LocalTime;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    public Subscription subscribe(SubscriptionRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail).get();
        subscriptionRepository.findByUser(user).ifPresent(existingSubscription -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "User already has an active subscription");
        });
        LocalDate today = LocalDate.now();
        if (request.getStartDate().isBefore(today)) {
            throw new ResponseStatusException(BAD_REQUEST, "Start date cannot be before today");
        }
        double rawHour = request.getReportHour();
        int hour = (int) rawHour;
        if (rawHour % 1 != 0||hour < 0 || hour > 23) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Report hour must be a number between 0 and 23.");
        }
        LocalTime reportTime = LocalTime.of(hour, 0);
        Subscription subscription = Subscription.builder()
                .user(user)
                .frequency(request.getFrequency())
                .startDate(request.getStartDate())
                .reportHour(reportTime)
                .build();
        return subscriptionRepository.save(subscription);
    }
    public void unsubscribe(String userEmail) {
        User user = userRepository.findByEmail(userEmail).get();
        Subscription subscription = subscriptionRepository.findByUser(user)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Subscription not found"));
        subscriptionRepository.delete(subscription);
    }
}
