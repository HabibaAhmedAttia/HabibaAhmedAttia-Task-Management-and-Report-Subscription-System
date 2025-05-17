package com.example.Task_Management_System.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
@Entity
@Table(name = "subscription")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Frequency frequency;
    @Column(nullable = false)
    private LocalDate startDate;
    @Column(nullable = false)
    private LocalTime reportHour;
    @OneToOne
    private User user;
    public enum Frequency {
        DAILY,
        WEEKLY,
        MONTHLY
    }
}
