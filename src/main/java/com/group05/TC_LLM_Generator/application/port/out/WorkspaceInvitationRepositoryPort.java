package com.group05.TC_LLM_Generator.application.port.out;

import com.group05.TC_LLM_Generator.domain.model.enums.InvitationStatus;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.WorkspaceInvitation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkspaceInvitationRepositoryPort {

    WorkspaceInvitation save(WorkspaceInvitation invitation);

    Optional<WorkspaceInvitation> findById(UUID invitationId);

    Optional<WorkspaceInvitation> findByToken(String token);

    List<WorkspaceInvitation> findByWorkspaceIdAndStatus(UUID workspaceId, InvitationStatus status);

    Page<WorkspaceInvitation> findByWorkspaceId(UUID workspaceId, Pageable pageable);

    boolean existsByWorkspaceIdAndEmailAndStatus(UUID workspaceId, String email, InvitationStatus status);

    void deleteById(UUID invitationId);
}
