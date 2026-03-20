package com.group05.TC_LLM_Generator.application.service;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.AuditLog;
import com.group05.TC_LLM_Generator.infrastructure.persistence.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

/**
 * Service for querying Audit Logs. Complements Grafana with structured API access.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    /**
     * Get all audit logs (admin, paginated).
     */
    public Page<AuditLog> getAll(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }

    /**
     * Get audit logs by entity type (e.g., PROJECT, USER_STORY, TEST_CASE).
     */
    public Page<AuditLog> getByEntityType(String entityType, Pageable pageable) {
        return auditLogRepository.findByEntityType(entityType, pageable);
    }

    /**
     * Get audit logs by user.
     */
    public Page<AuditLog> getByUser(UUID userId, Pageable pageable) {
        return auditLogRepository.findByUser_UserId(userId, pageable);
    }

    /**
     * Get audit logs by action (CREATED, UPDATED, DELETED).
     */
    public Page<AuditLog> getByAction(String action, Pageable pageable) {
        return auditLogRepository.findByAction(action, pageable);
    }

    /**
     * Get audit logs by time range.
     */
    public Page<AuditLog> getByTimeRange(Instant from, Instant to, Pageable pageable) {
        return auditLogRepository.findByCreatedAtBetween(from, to, pageable);
    }
}
