package com.group05.TC_LLM_Generator.application.service;

import com.group05.TC_LLM_Generator.application.port.out.WorkspaceMemberRepositoryPort;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserEntity;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.Workspace;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.WorkspaceMember;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkspaceMemberService {

    private final WorkspaceMemberRepositoryPort workspaceMemberRepository;

    @Transactional
    public WorkspaceMember addMember(Workspace workspace, UserEntity user, String role) {
        // Check if already a member
        if (workspaceMemberRepository.existsByWorkspaceIdAndUserId(
                workspace.getWorkspaceId(), user.getUserId())) {
            throw new IllegalArgumentException("User is already a member of this workspace");
        }

        WorkspaceMember member = WorkspaceMember.builder()
                .workspace(workspace)
                .user(user)
                .role(role)
                .joinedAt(Instant.now())
                .build();
        return workspaceMemberRepository.save(member);
    }

    public Optional<WorkspaceMember> getById(UUID workspaceMemberId) {
        return workspaceMemberRepository.findById(workspaceMemberId);
    }

    public List<WorkspaceMember> getByWorkspaceId(UUID workspaceId) {
        return workspaceMemberRepository.findByWorkspaceId(workspaceId);
    }

    public Page<WorkspaceMember> getByWorkspaceId(UUID workspaceId, Pageable pageable) {
        return workspaceMemberRepository.findByWorkspaceId(workspaceId, pageable);
    }

    public Optional<WorkspaceMember> getByWorkspaceIdAndUserId(UUID workspaceId, UUID userId) {
        return workspaceMemberRepository.findByWorkspaceIdAndUserId(workspaceId, userId);
    }

    public List<WorkspaceMember> getByUserId(UUID userId) {
        return workspaceMemberRepository.findByUserId(userId);
    }

    public boolean isMember(UUID workspaceId, UUID userId) {
        return workspaceMemberRepository.existsByWorkspaceIdAndUserId(workspaceId, userId);
    }

    public boolean isOwnerOrAdmin(UUID workspaceId, UUID userId) {
        Optional<WorkspaceMember> member = workspaceMemberRepository
                .findByWorkspaceIdAndUserId(workspaceId, userId);
        return member.map(m -> "Owner".equals(m.getRole()) || "Admin".equals(m.getRole()))
                .orElse(false);
    }

    @Transactional
    public WorkspaceMember updateRole(UUID workspaceMemberId, String newRole) {
        WorkspaceMember member = workspaceMemberRepository.findById(workspaceMemberId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Workspace member not found: " + workspaceMemberId));

        if ("Owner".equals(member.getRole())) {
            throw new IllegalArgumentException("Cannot change the role of the workspace owner");
        }

        member.setRole(newRole);
        return workspaceMemberRepository.save(member);
    }

    @Transactional
    public void removeMember(UUID workspaceMemberId) {
        WorkspaceMember member = workspaceMemberRepository.findById(workspaceMemberId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Workspace member not found: " + workspaceMemberId));

        if ("Owner".equals(member.getRole())) {
            throw new IllegalArgumentException("Cannot remove the workspace owner");
        }

        workspaceMemberRepository.deleteById(workspaceMemberId);
    }

    public long countMembers(UUID workspaceId) {
        return workspaceMemberRepository.countByWorkspaceId(workspaceId);
    }
}
