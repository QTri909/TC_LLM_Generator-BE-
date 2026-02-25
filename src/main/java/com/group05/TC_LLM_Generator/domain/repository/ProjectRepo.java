package com.group05.TC_LLM_Generator.domain.repository;

import com.group05.TC_LLM_Generator.domain.model.entity.ProjectDo;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepo {
    ProjectDo save(ProjectDo project);

    Optional<ProjectDo> findById(UUID id);
}
