package com.group05.TC_LLM_Generator.application.port.out;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.WorkspaceMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Output port for WorkspaceMember repository operations.
 */
public interface WorkspaceMemberRepositoryPort {

    WorkspaceMember save(WorkspaceMember workspaceMember);

    Optional<WorkspaceMember> findById(UUID workspaceMemberId);

    List<WorkspaceMember> findByWorkspaceId(UUID workspaceId);

    Page<WorkspaceMember> findByWorkspaceId(UUID workspaceId, Pageable pageable);

    Optional<WorkspaceMember> findByWorkspaceIdAndUserId(UUID workspaceId, UUID userId);

    List<WorkspaceMember> findByUserId(UUID userId);

    List<WorkspaceMember> findByWorkspaceIdAndRole(UUID workspaceId, String role);

    long countByWorkspaceId(UUID workspaceId);

    void deleteById(UUID workspaceMemberId);

    boolean existsById(UUID workspaceMemberId);

    boolean existsByWorkspaceIdAndUserId(UUID workspaceId, UUID userId);
}
