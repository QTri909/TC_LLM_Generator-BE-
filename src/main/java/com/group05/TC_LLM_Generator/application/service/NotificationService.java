package com.group05.TC_LLM_Generator.application.service;

import com.group05.TC_LLM_Generator.domain.model.enums.NotificationType;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.Notification;
import com.group05.TC_LLM_Generator.infrastructure.persistence.repository.NotificationRepository;
import com.group05.TC_LLM_Generator.presentation.dto.response.NotificationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Create a notification, persist it, and push it to the user via WebSocket.
     */
    @Transactional
    public Notification createAndPush(UUID recipientUserId,
                                       NotificationType type,
                                       String title,
                                       String message,
                                       String resourceType,
                                       UUID resourceId,
                                       UUID workspaceId,
                                       UUID projectId,
                                       String actorName) {
        Notification notification = Notification.builder()
                .recipientUserId(recipientUserId)
                .type(type)
                .title(title)
                .message(message)
                .resourceType(resourceType)
                .resourceId(resourceId)
                .workspaceId(workspaceId)
                .projectId(projectId)
                .actorName(actorName)
                .isRead(false)
                .build();

        notification = notificationRepository.save(notification);

        // Push real-time via WebSocket to the specific user
        NotificationResponse dto = toResponse(notification);
        try {
            messagingTemplate.convertAndSendToUser(
                    recipientUserId.toString(),
                    "/queue/notifications",
                    dto
            );
            log.debug("[Notification] Pushed to user {}: {}", recipientUserId, title);
        } catch (Exception e) {
            // WebSocket push failure should not break the flow
            log.warn("[Notification] Failed to push WS to user {}: {}", recipientUserId, e.getMessage());
        }

        return notification;
    }

    /**
     * Get paginated notifications for a user.
     */
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getNotifications(UUID userId, Pageable pageable) {
        return notificationRepository
                .findByRecipientUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::toResponse);
    }

    /**
     * Get unread notification count.
     */
    @Transactional(readOnly = true)
    public long getUnreadCount(UUID userId) {
        return notificationRepository.countByRecipientUserIdAndIsReadFalse(userId);
    }

    /**
     * Mark a single notification as read.
     */
    @Transactional
    public void markAsRead(UUID notificationId, UUID userId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            if (n.getRecipientUserId().equals(userId) && !n.isRead()) {
                n.setRead(true);
                notificationRepository.save(n);
            }
        });
    }

    /**
     * Mark all notifications as read for a user.
     */
    @Transactional
    public int markAllAsRead(UUID userId) {
        return notificationRepository.markAllAsReadForUser(userId);
    }

    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .notificationId(n.getNotificationId())
                .type(n.getType())
                .title(n.getTitle())
                .message(n.getMessage())
                .resourceType(n.getResourceType())
                .resourceId(n.getResourceId())
                .workspaceId(n.getWorkspaceId())
                .projectId(n.getProjectId())
                .actorName(n.getActorName())
                .isRead(n.isRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
