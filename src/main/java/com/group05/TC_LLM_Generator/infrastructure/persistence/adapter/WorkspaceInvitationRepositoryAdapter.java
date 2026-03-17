package com.group05.TC_LLM_Generator.infrastructure.persistence.adapter;

import com.group05.TC_LLM_Generator.application.port.out.WorkspaceInvitationRepositoryPort;
import com.group05.TC_LLM_Generator.domain.model.enums.InvitationStatus;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.WorkspaceInvitation;
import com.group05.TC_LLM_Generator.infrastructure.persistence.repository.WorkspaceInvitationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WorkspaceInvitationRepositoryAdapter implements WorkspaceInvitationRepositoryPort {

    private final WorkspaceInvitationRepository jpaRepository;

    @Override
    public WorkspaceInvitation save(WorkspaceInvitation invitation) {
        return jpaRepository.save(invitation);
    }

    @Override
    public Optional<WorkspaceInvitation> findById(UUID invitationId) {
        return jpaRepository.findById(invitationId);
    }

    @Override
    public Optional<WorkspaceInvitation> findByToken(String token) {
        return jpaRepository.findByToken(token);
    }

    @Override
    public List<WorkspaceInvitation> findByWorkspaceIdAndStatus(UUID workspaceId, InvitationStatus status) {
        return jpaRepository.findByWorkspace_WorkspaceIdAndStatus(workspaceId, status);
    }

    @Override
    public Page<WorkspaceInvitation> findByWorkspaceId(UUID workspaceId, Pageable pageable) {
        return jpaRepository.findByWorkspace_WorkspaceId(workspaceId, pageable);
    }

    @Override
    public boolean existsByWorkspaceIdAndEmailAndStatus(UUID workspaceId, String email, InvitationStatus status) {
        return jpaRepository.existsByWorkspace_WorkspaceIdAndInviteeEmailAndStatus(workspaceId, email, status);
    }

    @Override
    public void deleteById(UUID invitationId) {
        jpaRepository.deleteById(invitationId);
    }
}
