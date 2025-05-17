package com.example.Task_Management_System.service;

import com.example.Task_Management_System.entity.Subscription;
import com.example.Task_Management_System.entity.Task;
import com.example.Task_Management_System.entity.User;
import com.example.Task_Management_System.repository.SubscriptionRepository;
import com.example.Task_Management_System.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.time.LocalDate;
import java.util.List;
@Service
@RequiredArgsConstructor
public class ReportService {
    private final SubscriptionRepository subscriptionRepository;
    private final TaskRepository taskRepository;
    private final JavaMailSender mailSender;
    @Scheduled(cron = "0 0 * * * *")
    public void sendScheduledReports() {
        LocalDate now = LocalDate.now();
        List<Subscription> subscriptions = subscriptionRepository.findAll();
        for (Subscription sub : subscriptions) {
            LocalDate reportStartDate = switch (sub.getFrequency()) {
                case DAILY -> now.minusDays(1);
                case WEEKLY -> now.minusWeeks(1);
                case MONTHLY -> now.minusMonths(1);
            };

            List<Task> tasks = taskRepository.findByOwnerAndDueDateBetweenAndDeletedFalse(
                    sub.getUser(), reportStartDate, now);
            if (!tasks.isEmpty()) {
                try {
                    sendEmail(sub.getUser(), tasks);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void sendEmail(User user, List<Task> tasks) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(user.getEmail());
        helper.setSubject("📋 Task Report");
        StringBuilder content = new StringBuilder();
        content.append("<h2>Your Task Report</h2>");
        content.append("<table border='1' cellpadding='5' cellspacing='0'>");
        content.append("<tr><th>Title</th><th>Status</th><th>Due Date</th></tr>");
        for (Task task : tasks) {
            content.append("<tr>")
                    .append("<td>").append(task.getTitle()).append("</td>")
                    .append("<td>").append(task.getStatus()).append("</td>")
                    .append("<td>").append(task.getDueDate()).append("</td>")
                    .append("</tr>");
        }
        content.append("</table>");
        helper.setText(content.toString(), true);
        mailSender.send(message);
    }
}
