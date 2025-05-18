package com.example.Task_Management_System.dto;

import com.example.Task_Management_System.entity.Subscription;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
public class SubscriptionRequest {
    public interface OnCreate {}
    @NotNull(message = "Start date is required", groups = TaskRequest.OnCreate.class)
    private LocalDate startDate;

    @NotNull(message = "Frequency is required", groups = TaskRequest.OnCreate.class)
    private Subscription.Frequency frequency;

    @NotNull(message = "Report time is required", groups = TaskRequest.OnCreate.class)
    @Min(value = 0, message = "Hour must be between 0 and 23")
    @Max(value = 23, message = "Hour must be between 0 and 23")
    private Double reportHour;
}
