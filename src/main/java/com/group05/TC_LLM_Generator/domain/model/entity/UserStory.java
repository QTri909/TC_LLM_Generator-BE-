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
public class UserStory {
    private UUID id;
    private String jiraIssueKey;
    private String jiraIssueId;
    private String title;
    private String description;
    private String status;
    private Instant createdAt;
}
