package com.group05.TC_LLM_Generator.infrastructure.persistence.repository;

import com.group05.TC_LLM_Generator.domain.model.enums.InvitationStatus;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.WorkspaceInvitation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WorkspaceInvitationRepository extends JpaRepository<WorkspaceInvitation, UUID> {

    @EntityGraph(attributePaths = {"workspace", "inviterUser"})
    Optional<WorkspaceInvitation> findByToken(String token);

    @EntityGraph(attributePaths = {"workspace", "inviterUser"})
    List<WorkspaceInvitation> findByWorkspace_WorkspaceIdAndStatus(UUID workspaceId, InvitationStatus status);

    @EntityGraph(attributePaths = {"workspace", "inviterUser"})
    Page<WorkspaceInvitation> findByWorkspace_WorkspaceId(UUID workspaceId, Pageable pageable);

    Optional<WorkspaceInvitation> findByWorkspace_WorkspaceIdAndInviteeEmailAndStatus(
            UUID workspaceId, String inviteeEmail, InvitationStatus status);

    boolean existsByWorkspace_WorkspaceIdAndInviteeEmailAndStatus(
            UUID workspaceId, String inviteeEmail, InvitationStatus status);
}
