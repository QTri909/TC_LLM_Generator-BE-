package com.group05.TC_LLM_Generator.domain.model.enums;

/**
 * Enum for project member roles.
 * Stored as String in DB — use .name() for persistence, valueOf() for retrieval.
 */
public enum ProjectRole {
    Lead,
    Developer,
    Tester,
    Viewer;

    /**
     * Safely parse a role string, defaulting to Viewer if invalid.
     */
    public static ProjectRole fromString(String role) {
        try {
            return ProjectRole.valueOf(role);
        } catch (IllegalArgumentException | NullPointerException e) {
            return Viewer;
        }
    }
}
