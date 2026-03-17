package com.group05.TC_LLM_Generator.infrastructure.persistence.repository;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.WorkspaceMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for WorkspaceMember entity
 */
@Repository
public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, UUID> {

    @EntityGraph(attributePaths = {"workspace", "user"})
    List<WorkspaceMember> findByWorkspace_WorkspaceId(UUID workspaceId);

    @EntityGraph(attributePaths = {"workspace", "user"})
    Page<WorkspaceMember> findByWorkspace_WorkspaceId(UUID workspaceId, Pageable pageable);

    @EntityGraph(attributePaths = {"workspace", "user"})
    List<WorkspaceMember> findByUser_UserId(UUID userId);

    @EntityGraph(attributePaths = {"workspace", "user"})
    Optional<WorkspaceMember> findByWorkspace_WorkspaceIdAndUser_UserId(UUID workspaceId, UUID userId);

    List<WorkspaceMember> findByWorkspace_WorkspaceIdAndRole(UUID workspaceId, String role);

    long countByWorkspace_WorkspaceId(UUID workspaceId);
}
