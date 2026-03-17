package com.group05.TC_LLM_Generator.application.service;

import com.group05.TC_LLM_Generator.application.port.out.WorkspaceInvitationRepositoryPort;
import com.group05.TC_LLM_Generator.application.port.out.WorkspaceMemberRepositoryPort;
import com.group05.TC_LLM_Generator.application.port.out.authen.EmailSenderPort;
import com.group05.TC_LLM_Generator.domain.model.enums.InvitationStatus;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserEntity;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.Workspace;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.WorkspaceInvitation;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.WorkspaceMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkspaceInvitationService {

    private final WorkspaceInvitationRepositoryPort invitationRepository;
    private final WorkspaceMemberRepositoryPort memberRepository;
    private final EmailSenderPort emailSender;
    private final UserService userService;

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    @Transactional
    public WorkspaceInvitation sendInvitation(Workspace workspace, UserEntity inviter,
                                               String inviteeEmail, String role) {
        // Check if user is already a member
        Optional<UserEntity> existingUser = userService.getUserByEmail(inviteeEmail);
        if (existingUser.isPresent()) {
            boolean alreadyMember = memberRepository.existsByWorkspaceIdAndUserId(
                    workspace.getWorkspaceId(), existingUser.get().getUserId());
            if (alreadyMember) {
                throw new IllegalArgumentException("User with email " + inviteeEmail
                        + " is already a member of this workspace");
            }
        }

        // Check for existing pending invitation
        if (invitationRepository.existsByWorkspaceIdAndEmailAndStatus(
                workspace.getWorkspaceId(), inviteeEmail, InvitationStatus.PENDING)) {
            throw new IllegalArgumentException("A pending invitation already exists for " + inviteeEmail);
        }

        // Create invitation
        String token = UUID.randomUUID().toString();
        WorkspaceInvitation invitation = WorkspaceInvitation.builder()
                .workspace(workspace)
                .inviterUser(inviter)
                .inviteeEmail(inviteeEmail)
                .role(role)
                .token(token)
                .status(InvitationStatus.PENDING)
                .expiresAt(Instant.now().plus(7, ChronoUnit.DAYS))
                .build();

        invitation = invitationRepository.save(invitation);

        // Send email
        String acceptUrl = frontendUrl + "/invite/" + token;
        try {
            emailSender.sendWorkspaceInvitationEmail(
                    inviteeEmail, inviter.getFullName(), workspace.getName(), acceptUrl);
            log.info("Invitation email sent to {} for workspace {}", inviteeEmail, workspace.getName());
        } catch (Exception e) {
            log.error("Failed to send invitation email to {}", inviteeEmail, e);
            // Don't fail — invitation is already saved, user can still accept via link
        }

        return invitation;
    }

    @Transactional
    public WorkspaceMember acceptInvitation(String token, UserEntity acceptingUser) {
        WorkspaceInvitation invitation = invitationRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid invitation token"));

        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new IllegalArgumentException("Invitation is no longer pending (status: " + invitation.getStatus() + ")");
        }

        if (Instant.now().isAfter(invitation.getExpiresAt())) {
            invitation.setStatus(InvitationStatus.EXPIRED);
            invitationRepository.save(invitation);
            throw new IllegalArgumentException("Invitation has expired");
        }

        // Verify email matches
        if (!invitation.getInviteeEmail().equalsIgnoreCase(acceptingUser.getEmail())) {
            throw new IllegalArgumentException("This invitation was sent to a different email address");
        }

        // Check if already a member
        if (memberRepository.existsByWorkspaceIdAndUserId(
                invitation.getWorkspace().getWorkspaceId(), acceptingUser.getUserId())) {
            invitation.setStatus(InvitationStatus.ACCEPTED);
            invitation.setAcceptedAt(Instant.now());
            invitationRepository.save(invitation);
            throw new IllegalArgumentException("You are already a member of this workspace");
        }

        // Create workspace member
        WorkspaceMember member = WorkspaceMember.builder()
                .workspace(invitation.getWorkspace())
                .user(acceptingUser)
                .role(invitation.getRole())
                .joinedAt(Instant.now())
                .build();
        member = memberRepository.save(member);

        // Update invitation status
        invitation.setStatus(InvitationStatus.ACCEPTED);
        invitation.setAcceptedAt(Instant.now());
        invitationRepository.save(invitation);

        log.info("User {} accepted invitation and joined workspace {}",
                acceptingUser.getEmail(), invitation.getWorkspace().getName());

        return member;
    }

    public Optional<WorkspaceInvitation> getByToken(String token) {
        return invitationRepository.findByToken(token);
    }

    public List<WorkspaceInvitation> getPendingInvitations(UUID workspaceId) {
        return invitationRepository.findByWorkspaceIdAndStatus(workspaceId, InvitationStatus.PENDING);
    }

    public Page<WorkspaceInvitation> getByWorkspaceId(UUID workspaceId, Pageable pageable) {
        return invitationRepository.findByWorkspaceId(workspaceId, pageable);
    }

    @Transactional
    public void cancelInvitation(UUID invitationId) {
        WorkspaceInvitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new IllegalArgumentException("Invitation not found: " + invitationId));

        if (invitation.getStatus() != InvitationStatus.PENDING) {
            throw new IllegalArgumentException("Can only cancel pending invitations");
        }

        invitation.setStatus(InvitationStatus.CANCELLED);
        invitationRepository.save(invitation);
    }
}
