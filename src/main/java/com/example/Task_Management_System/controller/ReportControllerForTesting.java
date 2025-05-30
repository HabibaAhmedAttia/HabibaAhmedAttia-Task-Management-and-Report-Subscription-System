package com.example.Task_Management_System.controller;

import com.example.Task_Management_System.exception.ApiGenericResponse;
import com.example.Task_Management_System.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportControllerForTesting {
    private final ReportService reportService;

    @GetMapping("/test")
    public ResponseEntity<ApiGenericResponse<String>> triggerReportManually() {
        reportService.sendScheduledReports();
        return ResponseEntity.ok(ApiGenericResponse.success("✅ Report generation triggered manually.",null));
    }
}
