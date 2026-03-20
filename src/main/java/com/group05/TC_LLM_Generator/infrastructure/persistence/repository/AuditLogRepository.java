package com.group05.TC_LLM_Generator.infrastructure.persistence.repository;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for AuditLog entity
 */
@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    List<AuditLog> findByUser_UserId(UUID userId);
    Page<AuditLog> findByUser_UserId(UUID userId, Pageable pageable);

    List<AuditLog> findByEntityType(String entityType);
    Page<AuditLog> findByEntityType(String entityType, Pageable pageable);

    List<AuditLog> findByEntityId(UUID entityId);

    List<AuditLog> findByEntityTypeAndEntityId(String entityType, UUID entityId);

    List<AuditLog> findByAction(String action);
    Page<AuditLog> findByAction(String action, Pageable pageable);

    List<AuditLog> findAllByOrderByCreatedAtDesc();

    List<AuditLog> findByUser_UserIdAndCreatedAtBetween(UUID userId, Instant startTime, Instant endTime);

    Page<AuditLog> findByCreatedAtBetween(Instant startTime, Instant endTime, Pageable pageable);
}
