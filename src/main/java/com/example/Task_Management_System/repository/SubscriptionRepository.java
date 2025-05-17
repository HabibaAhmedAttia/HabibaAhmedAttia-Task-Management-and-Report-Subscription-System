package com.example.Task_Management_System.repository;

import com.example.Task_Management_System.entity.Subscription;
import com.example.Task_Management_System.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {
    Optional<Subscription> findByUser(User user);
    List<Subscription> findAll();
}
