package com.group05.TC_LLM_Generator.infrastructure.persistence.adapter;

import com.group05.TC_LLM_Generator.application.port.out.WorkspaceMemberRepositoryPort;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.WorkspaceMember;
import com.group05.TC_LLM_Generator.infrastructure.persistence.repository.WorkspaceMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WorkspaceMemberRepositoryAdapter implements WorkspaceMemberRepositoryPort {

    private final WorkspaceMemberRepository jpaRepository;

    @Override
    public WorkspaceMember save(WorkspaceMember workspaceMember) {
        return jpaRepository.save(workspaceMember);
    }

    @Override
    public Optional<WorkspaceMember> findById(UUID workspaceMemberId) {
        return jpaRepository.findById(workspaceMemberId);
    }

    @Override
    public List<WorkspaceMember> findByWorkspaceId(UUID workspaceId) {
        return jpaRepository.findByWorkspace_WorkspaceId(workspaceId);
    }

    @Override
    public Page<WorkspaceMember> findByWorkspaceId(UUID workspaceId, Pageable pageable) {
        return jpaRepository.findByWorkspace_WorkspaceId(workspaceId, pageable);
    }

    @Override
    public Optional<WorkspaceMember> findByWorkspaceIdAndUserId(UUID workspaceId, UUID userId) {
        return jpaRepository.findByWorkspace_WorkspaceIdAndUser_UserId(workspaceId, userId);
    }

    @Override
    public List<WorkspaceMember> findByUserId(UUID userId) {
        return jpaRepository.findByUser_UserId(userId);
    }

    @Override
    public List<WorkspaceMember> findByWorkspaceIdAndRole(UUID workspaceId, String role) {
        return jpaRepository.findByWorkspace_WorkspaceIdAndRole(workspaceId, role);
    }

    @Override
    public long countByWorkspaceId(UUID workspaceId) {
        return jpaRepository.countByWorkspace_WorkspaceId(workspaceId);
    }

    @Override
    public void deleteById(UUID workspaceMemberId) {
        jpaRepository.deleteById(workspaceMemberId);
    }

    @Override
    public boolean existsById(UUID workspaceMemberId) {
        return jpaRepository.existsById(workspaceMemberId);
    }

    @Override
    public boolean existsByWorkspaceIdAndUserId(UUID workspaceId, UUID userId) {
        return jpaRepository.findByWorkspace_WorkspaceIdAndUser_UserId(workspaceId, userId).isPresent();
    }
}
