package com.group05.TC_LLM_Generator.infrastructure.persistence.repository;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.AuditLog;
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

    /**
     * Find audit logs by user ID
     * @param userId user ID
     * @return List of audit logs
     */
    List<AuditLog> findByUser_UserId(UUID userId);

    /**
     * Find audit logs by entity type
     * @param entityType entity type
     * @return List of audit logs
     */
    List<AuditLog> findByEntityType(String entityType);

    /**
     * Find audit logs by entity ID
     * @param entityId entity ID
     * @return List of audit logs
     */
    List<AuditLog> findByEntityId(UUID entityId);

    /**
     * Find audit logs by entity type and entity ID
     * @param entityType entity type
     * @param entityId entity ID
     * @return List of audit logs
     */
    List<AuditLog> findByEntityTypeAndEntityId(String entityType, UUID entityId);

    /**
     * Find audit logs by action
     * @param action audit action
     * @return List of audit logs
     */
    List<AuditLog> findByAction(String action);

    /**
     * Find audit logs ordered by timestamp
     * @return List of audit logs ordered by timestamp descending
     */
    List<AuditLog> findAllByOrderByTimestampDesc();

    /**
     * Find audit logs by user ID within a time range
     * @param userId user ID
     * @param startTime start timestamp
     * @param endTime end timestamp
     * @return List of audit logs
     */
    List<AuditLog> findByUser_UserIdAndTimestampBetween(UUID userId, Instant startTime, Instant endTime);
}
