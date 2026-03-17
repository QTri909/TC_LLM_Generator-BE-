package com.group05.TC_LLM_Generator.domain.model.enums;

/**
 * Enum for workspace member roles.
 * Stored as String in DB — use .name() for persistence, valueOf() for retrieval.
 */
public enum WorkspaceRole {
    Owner,
    Admin,
    Member;

    /**
     * Safely parse a role string, defaulting to Member if invalid.
     */
    public static WorkspaceRole fromString(String role) {
        try {
            return WorkspaceRole.valueOf(role);
        } catch (IllegalArgumentException | NullPointerException e) {
            return Member;
        }
    }
}
