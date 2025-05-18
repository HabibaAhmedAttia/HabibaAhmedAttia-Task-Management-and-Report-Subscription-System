package com.example.Task_Management_System.controller;

import com.example.Task_Management_System.dto.SubscriptionRequest;
import com.example.Task_Management_System.entity.Subscription;
import com.example.Task_Management_System.exception.ApiGenericResponse;
import com.example.Task_Management_System.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/subscription")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    @Operation(summary = "Subscribe to report generation",
            description = "Allows an authenticated user to subscribe to periodic task reports (daily, weekly, or monthly). "
                    + "Reports include tasks with a due date in the selected period. The report will be sent to the user's email at the selected hour each day/week/month.")
    @PostMapping
    public ResponseEntity<ApiGenericResponse<Subscription>> subscribe(
            @Valid @RequestBody SubscriptionRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        Subscription subscription = subscriptionService.subscribe(request, email);
        LocalDate frequencyPeriodStartDate = switch (subscription.getFrequency()) {
            case DAILY -> subscription.getStartDate().minusDays(1);
            case WEEKLY -> subscription.getStartDate().minusWeeks(1);
            case MONTHLY -> subscription.getStartDate().minusMonths(1);
        };
        if (frequencyPeriodStartDate.isBefore(subscription.getStartDate())) {
            frequencyPeriodStartDate = subscription.getStartDate();
        }
        String frequencyWord=switch (subscription.getFrequency()) {
            case DAILY -> frequencyWord = "day";
            case WEEKLY -> frequencyWord = "week";
            case MONTHLY -> frequencyWord = "month";
        };
        String detailedMessage = String.format(
                "Subscribed successfully! Reports will be sent starting from %s at %02d:00 every %s.Including tasks with end date in the last %s",
                subscription.getStartDate(),
                subscription.getReportHour().getHour(),
                frequencyWord,
                frequencyWord
        );
        ApiGenericResponse<Subscription> response = ApiGenericResponse.success(detailedMessage, subscription);
        return ResponseEntity.ok(response);
    }
    @Operation(summary = "Unsubscribe from report generation",
            description = "Allows the authenticated user to stop receiving periodic reports. "
                    + "Deletes the user's current report subscription if it exists.")
    @DeleteMapping
    public ResponseEntity<ApiGenericResponse<Void>> unsubscribe(Authentication authentication) {
        String email = authentication.getName();
        subscriptionService.unsubscribe(email);
        ApiGenericResponse<Void> response = ApiGenericResponse.success("Unsubscribed successfully", null);
        return ResponseEntity.ok(response);
    }
}
