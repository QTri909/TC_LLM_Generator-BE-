package com.group05.TC_LLM_Generator.presentation.dto.response;

import com.group05.TC_LLM_Generator.domain.model.enums.NotificationType;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {
    private UUID notificationId;
    private NotificationType type;
    private String title;
    private String message;
    private String resourceType;
    private UUID resourceId;
    private UUID workspaceId;
    private UUID projectId;
    private String actorName;
    private boolean isRead;
    private Instant createdAt;
}
