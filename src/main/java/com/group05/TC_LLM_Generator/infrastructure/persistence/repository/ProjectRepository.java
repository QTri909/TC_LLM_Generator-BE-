package com.group05.TC_LLM_Generator.infrastructure.persistence.repository;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Project entity
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {

    /**
     * Find project by project key
     * @param projectKey unique project key
     * @return Optional of Project
     */
    Optional<Project> findByProjectKey(String projectKey);

    /**
     * Find projects by workspace ID
     * @param workspaceId workspace ID
     * @return List of projects
     */
    List<Project> findByWorkspace_WorkspaceId(UUID workspaceId);

    /**
     * Find projects by status
     * @param status project status
     * @return List of projects with the specified status
     */
    List<Project> findByStatus(String status);

    /**
     * Find projects by workspace ID and status
     * @param workspaceId workspace ID
     * @param status project status
     * @return List of projects
     */
    List<Project> findByWorkspace_WorkspaceIdAndStatus(UUID workspaceId, String status);

    /**
     * Find projects by created by user ID
     * @param userId user ID
     * @return List of projects created by the user
     */
    List<Project> findByCreatedByUser_UserId(UUID userId);

    /**
     * Check if project key exists
     * @param projectKey project key
     * @return true if exists
     */
    boolean existsByProjectKey(String projectKey);

    /**
     * Find projects by Jira site ID
     * @param jiraSiteId Jira site ID
     * @return List of projects
     */
    List<Project> findByJiraSiteId(String jiraSiteId);
}
