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
public class WorkspaceMemberResponse extends RepresentationModel<WorkspaceMemberResponse> {

    private UUID workspaceMemberId;
    private UUID workspaceId;
    private String workspaceName;
    private UUID userId;
    private String userFullName;
    private String userEmail;
    private String role;
    private Instant joinedAt;
}
