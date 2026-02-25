package com.group05.TC_LLM_Generator.domain.repository;

import com.group05.TC_LLM_Generator.domain.model.entity.Workspace;

import java.util.Optional;
import java.util.UUID;

public interface WorkspaceRepo {
    Optional<Workspace> findById(UUID id);

    Optional<Workspace> findFirstByOwnerId(UUID ownerId);

    Workspace save(Workspace workspace);
}
