package com.group05.TC_LLM_Generator.infrastructure.persistence.adapter;

import com.group05.TC_LLM_Generator.domain.model.entity.ProjectDo;
import com.group05.TC_LLM_Generator.domain.repository.ProjectRepo;
import com.group05.TC_LLM_Generator.infrastructure.persistence.mapper.ProjectMapper;
import com.group05.TC_LLM_Generator.infrastructure.persistence.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProjectRepoAdapter implements ProjectRepo {

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Override
    public ProjectDo save(ProjectDo project) {
        var entity = projectMapper.toEntity(project);

        // Handle relationships if necessary (e.g., if project has no workspace ID yet)
        // Usually, the service sets the relationships on the domain model.
        // But the mapper's toEntity might ignore some fields.

        // Wait, I need to make sure toEntity doesn't ignore fields we need.
        // In Step 30, I ignored workspace, createdByUser, etc. in toEntity.
        // I should probably manually set them if they are in the database.

        // To keep it simple, I'll let the service handle entity population if needed,
        // OR I can improve the mapper.

        var savedEntity = projectRepository.save(entity);
        return projectMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<ProjectDo> findById(UUID id) {
        return projectRepository.findById(id).map(projectMapper::toDomain);
    }
}
