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
public class Workspace {
    private UUID id;
    private UUID ownerId;
    private String name;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
    private java.util.List<Project> projects;
}
