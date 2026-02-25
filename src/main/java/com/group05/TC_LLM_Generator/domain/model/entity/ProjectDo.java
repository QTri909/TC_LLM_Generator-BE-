package com.group05.TC_LLM_Generator.domain.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDo {
    private UUID id;
    private String projectKey;
    private String name;
    private String description;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;
    private List<ProjectMember> members;
    private List<BusinessRule> businessRules;
    private List<UserStory> userStories;
}
