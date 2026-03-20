package com.group05.TC_LLM_Generator.application.service;

import com.group05.TC_LLM_Generator.domain.model.enums.ProjectRole;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.ProjectMember;
import com.group05.TC_LLM_Generator.infrastructure.persistence.repository.ProjectMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;

/**
 * Centralized service for checking project-level access control.
 * Used by controllers that need to verify user membership / role in a project.
 */
@Service
@RequiredArgsConstructor
public class ProjectAccessChecker {

    private final ProjectMemberRepository projectMemberRepository;

    /**
     * Require the user to be a member of the project (any role).
     * @throws AccessDeniedException if user is not a member
     */
    public ProjectMember requireProjectMember(UUID projectId, UUID userId) {
        return projectMemberRepository.findByProject_ProjectIdAndUser_UserId(projectId, userId)
                .orElseThrow(() -> new AccessDeniedException(
                        "You are not a member of this project"));
    }

    /**
     * Require contributor-level access (Lead, Developer, or Tester).
     * Viewers are excluded.
     */
    public ProjectMember requireContributor(UUID projectId, UUID userId) {
        ProjectMember member = requireProjectMember(projectId, userId);
        ProjectRole role = ProjectRole.fromString(member.getRole());
        if (role == ProjectRole.Viewer) {
            throw new AccessDeniedException(
                    "You need contributor access (Lead/Developer/Tester) for this action");
        }
        return member;
    }

    /**
     * Require Lead-level access.
     */
    public ProjectMember requireLead(UUID projectId, UUID userId) {
        ProjectMember member = requireProjectMember(projectId, userId);
        ProjectRole role = ProjectRole.fromString(member.getRole());
        if (role != ProjectRole.Lead) {
            throw new AccessDeniedException(
                    "Only the project Lead can perform this action");
        }
        return member;
    }

    /**
     * Require one of the specified roles.
     */
    public ProjectMember requireRole(UUID projectId, UUID userId, Set<ProjectRole> allowedRoles) {
        ProjectMember member = requireProjectMember(projectId, userId);
        ProjectRole role = ProjectRole.fromString(member.getRole());
        if (!allowedRoles.contains(role)) {
            throw new AccessDeniedException(
                    "You do not have the required role for this action. Required: " + allowedRoles);
        }
        return member;
    }
}
