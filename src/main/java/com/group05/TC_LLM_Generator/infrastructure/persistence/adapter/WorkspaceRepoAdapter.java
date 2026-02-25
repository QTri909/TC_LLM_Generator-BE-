package com.group05.TC_LLM_Generator.infrastructure.persistence.adapter;

import com.group05.TC_LLM_Generator.domain.model.entity.Workspace;
import com.group05.TC_LLM_Generator.domain.repository.WorkspaceRepo;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserEntity;
import com.group05.TC_LLM_Generator.infrastructure.persistence.mapper.WorkspaceMapper;
import com.group05.TC_LLM_Generator.infrastructure.persistence.repository.ProjectRepository;
import com.group05.TC_LLM_Generator.infrastructure.persistence.repository.UserRepository;
import com.group05.TC_LLM_Generator.infrastructure.persistence.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class WorkspaceRepoAdapter implements WorkspaceRepo {

    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final WorkspaceMapper workspaceMapper;

    @Override
    public Optional<Workspace> findById(UUID id) {
        return workspaceRepository.findById(id).map(workspace -> {
            var projects = projectRepository.findByWorkspace_WorkspaceId(id);
            workspace.setProjects(projects);
            return workspaceMapper.toDomain(workspace);
        });
    }

    @Override
    public Optional<Workspace> findFirstByOwnerId(UUID ownerId) {
        return workspaceRepository.findFirstByOwnerUser_UserId(ownerId).map(workspace -> {
            var projects = projectRepository.findByWorkspace_WorkspaceId(workspace.getWorkspaceId());
            workspace.setProjects(projects);
            return workspaceMapper.toDomain(workspace);
        });
    }

    @Override
    public Workspace save(Workspace workspace) {
        UserEntity owner = userRepository.findById(workspace.getOwnerId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + workspace.getOwnerId()));

        var entity = workspaceMapper.toEntity(workspace, owner);
        var savedEntity = workspaceRepository.save(entity);
        return workspaceMapper.toDomain(savedEntity);
    }
}
