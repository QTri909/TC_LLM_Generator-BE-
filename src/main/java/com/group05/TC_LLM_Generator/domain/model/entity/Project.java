package com.group05.TC_LLM_Generator.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Project {
    private UUID id;
    private UUID workspaceId;
    private UUID createdByUserId;
    private String projectKey;
    private String name;
    private String description;
    private String jiraSiteId;
    private String jiraProjectKey;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;
    private java.util.List<ProjectMember> members;
}
