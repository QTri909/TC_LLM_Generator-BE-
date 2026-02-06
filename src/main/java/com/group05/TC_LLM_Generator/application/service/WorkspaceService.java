package com.group05.TC_LLM_Generator.application.service;

import com.group05.TC_LLM_Generator.application.port.in.GetWorkspaceUseCase;
import com.group05.TC_LLM_Generator.domain.model.entity.Workspace;
import com.group05.TC_LLM_Generator.domain.repository.WorkspaceRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkspaceService implements GetWorkspaceUseCase {

    private final WorkspaceRepo workspaceRepo;

    @Override
    @Transactional
    public List<Workspace> getWorkspaceForUser(UUID userId) {
        List<Workspace> workspaces = workspaceRepo.findAllByOwnerId(userId);

        if (workspaces.isEmpty()) {
            Workspace defaultWorkspace = Workspace.builder()
                    .ownerId(userId)
                    .name("Default Workspace")
                    .description("Auto-generated default workspace")
                    .build();
            workspaceRepo.save(defaultWorkspace);
            workspaces.add(defaultWorkspace);
        }

        return workspaces;
    }
}
