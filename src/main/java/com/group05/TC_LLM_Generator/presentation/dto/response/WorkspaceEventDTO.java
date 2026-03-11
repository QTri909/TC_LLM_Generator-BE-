package com.group05.TC_LLM_Generator.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for broadcasting workspace CRUD events over WebSocket.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkspaceEventDTO {

    /**
     * Action type: CREATED, UPDATED, DELETED
     */
    private String action;

    /**
     * The workspace data (null for DELETE events, which only need workspaceId)
     */
    private WorkspaceResponse workspace;

    /**
     * Workspace ID (useful for DELETE events where workspace is null)
     */
    private String workspaceId;

    /**
     * User ID of who performed the action
     */
    private String performedBy;
}
