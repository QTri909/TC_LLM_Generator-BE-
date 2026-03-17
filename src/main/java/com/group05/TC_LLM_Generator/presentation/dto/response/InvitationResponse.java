package com.group05.TC_LLM_Generator.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.Instant;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvitationResponse extends RepresentationModel<InvitationResponse> {

    private UUID invitationId;
    private UUID workspaceId;
    private String workspaceName;
    private UUID inviterUserId;
    private String inviterName;
    private String inviteeEmail;
    private String role;
    private String status;
    private Instant expiresAt;
    private Instant createdAt;
    private Instant acceptedAt;
}
