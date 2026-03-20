package com.group05.TC_LLM_Generator.presentation.controller;

import com.group05.TC_LLM_Generator.application.service.ProjectAccessChecker;
import com.group05.TC_LLM_Generator.application.service.ReportService;
import com.group05.TC_LLM_Generator.presentation.dto.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final ProjectAccessChecker accessChecker;

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getReport(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID projectId) {

        UUID userId = UUID.fromString(jwt.getSubject());
        accessChecker.requireProjectMember(projectId, userId);

        Map<String, Object> report = reportService.getFullReport(projectId);
        return ResponseEntity.ok(ApiResponse.success(report, "Report retrieved successfully"));
    }
}
