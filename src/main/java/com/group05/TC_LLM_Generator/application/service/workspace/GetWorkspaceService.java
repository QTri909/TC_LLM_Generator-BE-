package com.group05.TC_LLM_Generator.application.service.workspace;

import com.group05.TC_LLM_Generator.application.port.in.workspace.GetWorkspaceUseCase;
import com.group05.TC_LLM_Generator.domain.model.entity.Workspace;
import com.group05.TC_LLM_Generator.domain.repository.WorkspaceRepo;

import com.group05.TC_LLM_Generator.infrastructure.persistence.repository.UserRepository;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserEntity;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class GetWorkspaceService implements GetWorkspaceUseCase {

    private final WorkspaceRepo workspaceRepo;
    private final UserRepository userRepository;

    @Override
    public Optional<Workspace> execute(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getLastActiveWorkspaceId() != null) {
            Optional<Workspace> workspace = workspaceRepo.findById(user.getLastActiveWorkspaceId());
            if (workspace.isPresent()) {
                return workspace;
            }
        }

        // If no last active workspace OR workspace deleted, pick the first one
        Optional<Workspace> workspace = workspaceRepo.findFirstByOwnerId(userId);

        if (workspace.isEmpty()) {
            Workspace defaultWorkspace = Workspace.builder()
                    .ownerId(userId)
                    .name("Default Workspace")
                    .description("Auto-generated default workspace")
                    .build();
            workspace = Optional.of(workspaceRepo.save(defaultWorkspace));
        }

        // Update last active workspace for the user
        user.setLastActiveWorkspaceId(workspace.get().getId());
        userRepository.save(user);

        return workspace;
    }

}
