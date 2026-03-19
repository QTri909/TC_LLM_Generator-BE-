package com.group05.TC_LLM_Generator.infrastructure.config;

import com.group05.TC_LLM_Generator.application.service.NotificationService;
import com.group05.TC_LLM_Generator.domain.event.EntityChangedEvent;
import com.group05.TC_LLM_Generator.domain.model.enums.NotificationType;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.ProjectMember;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserEntity;
import com.group05.TC_LLM_Generator.infrastructure.persistence.repository.ProjectMemberRepository;
import com.group05.TC_LLM_Generator.infrastructure.persistence.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Listens for {@link EntityChangedEvent} and creates in-app notifications
 * for relevant users. Runs asynchronously to avoid slowing down the main
 * request thread.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;
    private final ProjectMemberRepository projectMemberRepository;
    private final UserRepository userRepository;

    @Async
    @EventListener
    public void handleEntityChanged(EntityChangedEvent event) {
        try {
            switch (event.getEntityType()) {
                case STORY -> handleStoryEvent(event);
                case PROJECT -> handleProjectEvent(event);
                case WORKSPACE_MEMBER -> handleWorkspaceMemberEvent(event);
                case TEST_PLAN -> handleTestPlanEvent(event);
                default -> { /* No notifications for other entity types yet */ }
            }
        } catch (Exception e) {
            log.warn("[NotificationEventListener] Failed to create notifications for {}.{}: {}",
                    event.getEntityType(), event.getAction(), e.getMessage());
        }
    }

    private void handleStoryEvent(EntityChangedEvent event) {
        if (event.getParentId() == null || event.getPerformedBy() == null) return;
        if ("system".equals(event.getPerformedBy())) return; // Skip auto-transitions

        UUID projectId = UUID.fromString(event.getParentId());
        UUID performerId = UUID.fromString(event.getPerformedBy());
        String actorName = resolveActorName(performerId);

        NotificationType type;
        String title;
        String message;

        switch (event.getAction()) {
            case CREATED -> {
                type = NotificationType.STORY_CREATED;
                title = "New User Story";
                message = actorName + " created a new user story";
            }
            case UPDATED -> {
                type = NotificationType.STORY_UPDATED;
                title = "Story Updated";
                message = actorName + " updated a user story";
            }
            case DELETED -> {
                type = NotificationType.STORY_DELETED;
                title = "Story Deleted";
                message = actorName + " deleted a user story";
            }
            default -> { return; }
        }

        notifyProjectMembers(projectId, performerId, type, title, message,
                "STORY", UUID.fromString(event.getEntityId()), null, projectId, actorName);
    }

    private void handleTestPlanEvent(EntityChangedEvent event) {
        if (event.getParentId() == null || event.getPerformedBy() == null) return;
        if ("system".equals(event.getPerformedBy())) return;

        UUID projectId = UUID.fromString(event.getParentId());
        UUID performerId = UUID.fromString(event.getPerformedBy());
        String actorName = resolveActorName(performerId);

        if (event.getAction() == EntityChangedEvent.Action.CREATED) {
            notifyProjectMembers(projectId, performerId,
                    NotificationType.TEST_PLAN_CREATED,
                    "New Test Plan",
                    actorName + " created a new test plan",
                    "TEST_PLAN", UUID.fromString(event.getEntityId()),
                    null, projectId, actorName);
        }
    }

    private void handleProjectEvent(EntityChangedEvent event) {
        // Only notify on project creation (workspace members get notified)
        if (event.getAction() != EntityChangedEvent.Action.CREATED) return;
        if (event.getPerformedBy() == null) return;

        // For project events, parentId is workspaceId — we can skip this for now
        // since project creation is already visible in workspace view
    }

    private void handleWorkspaceMemberEvent(EntityChangedEvent event) {
        if (event.getAction() != EntityChangedEvent.Action.CREATED) return;
        if (event.getPerformedBy() == null) return;

        // The entityId is the new member's ID — notify them that they were added
        // Note: workspace member events don't directly give us the userId of the added member,
        // so we skip this for now. Invitation system already handles this separately.
    }

    /**
     * Send notification to all project members except the performer.
     */
    private void notifyProjectMembers(UUID projectId, UUID excludeUserId,
                                       NotificationType type, String title, String message,
                                       String resourceType, UUID resourceId,
                                       UUID workspaceId, UUID projId, String actorName) {
        List<ProjectMember> members = projectMemberRepository.findByProject_ProjectId(projectId);

        for (ProjectMember member : members) {
            UUID recipientId = member.getUser().getUserId();
            if (recipientId.equals(excludeUserId)) continue; // Don't notify the performer

            notificationService.createAndPush(
                    recipientId, type, title, message,
                    resourceType, resourceId, workspaceId, projId, actorName
            );
        }
    }

    private String resolveActorName(UUID userId) {
        return userRepository.findById(userId)
                .map(UserEntity::getFullName)
                .orElse("Someone");
    }
}
