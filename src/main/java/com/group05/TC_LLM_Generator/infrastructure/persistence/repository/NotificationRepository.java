package com.group05.TC_LLM_Generator.infrastructure.persistence.repository;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    Page<Notification> findByRecipientUserIdOrderByCreatedAtDesc(UUID recipientUserId, Pageable pageable);

    // 7-day scoped queries
    Page<Notification> findByRecipientUserIdAndCreatedAtAfterOrderByCreatedAtDesc(
            UUID recipientUserId, Instant since, Pageable pageable);

    long countByRecipientUserIdAndIsReadFalse(UUID recipientUserId);

    long countByRecipientUserIdAndIsReadFalseAndCreatedAtAfter(
            UUID recipientUserId, Instant since);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.recipientUserId = :userId AND n.isRead = false")
    int markAllAsReadForUser(@Param("userId") UUID userId);
}
