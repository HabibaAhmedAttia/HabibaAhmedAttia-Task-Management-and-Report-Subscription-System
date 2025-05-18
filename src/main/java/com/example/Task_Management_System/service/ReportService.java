package com.example.Task_Management_System.service;

import com.example.Task_Management_System.entity.Subscription;
import com.example.Task_Management_System.entity.Task;
import com.example.Task_Management_System.entity.User;
import com.example.Task_Management_System.repository.SubscriptionRepository;
import com.example.Task_Management_System.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
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
        int currentHour = LocalTime.now().getHour();
        List<Subscription> subscriptions = subscriptionRepository.findAll();
        for (Subscription sub : subscriptions) {
            if (sub.getReportHour().getHour() != currentHour||now.isBefore(sub.getStartDate())) continue;
            LocalDate periodStartDate = switch (sub.getFrequency()) {
                case DAILY -> now.minusDays(1);
                case WEEKLY -> now.minusWeeks(1);
                case MONTHLY -> now.minusMonths(1);
            };
            if (now.equals(sub.getStartDate())) {
                periodStartDate = switch (sub.getFrequency()) {
                    case DAILY -> sub.getStartDate().minusDays(1);
                    case WEEKLY -> sub.getStartDate().minusWeeks(1);
                    case MONTHLY -> sub.getStartDate().minusMonths(1);
                };
            }
            if (periodStartDate.isBefore(sub.getStartDate())) {
                periodStartDate = sub.getStartDate();
            }
            List<Task> tasks = taskRepository.findByOwnerAndDueDateBetweenAndDeletedFalse(
                    sub.getUser(), periodStartDate, now);
            try {
                if (!tasks.isEmpty()) {
                    sendTasksEmail(sub.getUser(), tasks);
                } else {
                    sendNoTasksEmail(sub.getUser());
                }
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }
    private void sendTasksEmail(User user, List<Task> tasks) throws MessagingException {
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
    private void sendNoTasksEmail(User user) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(user.getEmail());
        helper.setSubject("📋 Task Report - No Tasks Available");
        String content = "<h2>Your Task Report</h2>" +
                "<p>There are currently no tasks with due dates in your selected period.</p>";
        helper.setText(content, true);
        mailSender.send(message);
    }
}
