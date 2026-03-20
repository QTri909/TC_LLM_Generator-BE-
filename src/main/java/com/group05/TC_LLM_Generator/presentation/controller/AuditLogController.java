package com.group05.TC_LLM_Generator.presentation.controller;

import com.group05.TC_LLM_Generator.application.service.AuditLogService;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.AuditLog;
import com.group05.TC_LLM_Generator.presentation.dto.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Admin-only controller for querying Audit Logs.
 * Complements Grafana with structured REST API access.
 */
@RestController
@RequestMapping("/api/v1/admin/audit-logs")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    /**
     * List all audit logs (paginated)
     * GET /api/v1/admin/audit-logs
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<AuditLog>>> getAll(
            @PageableDefault(size = 20, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<AuditLog> logs = auditLogService.getAll(pageable);
        return ResponseEntity.ok(ApiResponse.success(logs));
    }

    /**
     * Filter by entity type
     * GET /api/v1/admin/audit-logs?entityType=PROJECT
     */
    @GetMapping(params = "entityType")
    public ResponseEntity<ApiResponse<Page<AuditLog>>> getByEntityType(
            @RequestParam String entityType,
            @PageableDefault(size = 20, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<AuditLog> logs = auditLogService.getByEntityType(entityType, pageable);
        return ResponseEntity.ok(ApiResponse.success(logs));
    }

    /**
     * Filter by user
     * GET /api/v1/admin/audit-logs/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Page<AuditLog>>> getByUser(
            @PathVariable UUID userId,
            @PageableDefault(size = 20, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<AuditLog> logs = auditLogService.getByUser(userId, pageable);
        return ResponseEntity.ok(ApiResponse.success(logs));
    }

    /**
     * Filter by action (CREATED, UPDATED, DELETED)
     * GET /api/v1/admin/audit-logs?action=CREATED
     */
    @GetMapping(params = "action")
    public ResponseEntity<ApiResponse<Page<AuditLog>>> getByAction(
            @RequestParam String action,
            @PageableDefault(size = 20, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<AuditLog> logs = auditLogService.getByAction(action, pageable);
        return ResponseEntity.ok(ApiResponse.success(logs));
    }

    /**
     * Filter by time range
     * GET /api/v1/admin/audit-logs?from=2026-01-01T00:00:00Z&to=2026-12-31T23:59:59Z
     */
    @GetMapping(params = {"from", "to"})
    public ResponseEntity<ApiResponse<Page<AuditLog>>> getByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @PageableDefault(size = 20, sort = "timestamp", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<AuditLog> logs = auditLogService.getByTimeRange(from, to, pageable);
        return ResponseEntity.ok(ApiResponse.success(logs));
    }
}
