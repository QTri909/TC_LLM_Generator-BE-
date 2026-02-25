package com.group05.TC_LLM_Generator.application.service.project;

import com.group05.TC_LLM_Generator.application.port.in.project.CreateProjectUseCase;
import com.group05.TC_LLM_Generator.application.port.in.project.UpdateProjectDetailsUseCase;
import com.group05.TC_LLM_Generator.domain.model.entity.BusinessRule;
import com.group05.TC_LLM_Generator.domain.model.entity.ProjectDo;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.Project;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestCaseTemplate;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserEntity;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.Workspace;
import com.group05.TC_LLM_Generator.infrastructure.persistence.mapper.ProjectMapper;
import com.group05.TC_LLM_Generator.infrastructure.persistence.repository.ProjectRepository;
import com.group05.TC_LLM_Generator.infrastructure.persistence.repository.TestCaseTemplateRepository;
import com.group05.TC_LLM_Generator.infrastructure.persistence.repository.UserRepository;
import com.group05.TC_LLM_Generator.infrastructure.persistence.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectService implements CreateProjectUseCase, UpdateProjectDetailsUseCase {

    private final ProjectRepository projectRepository;
    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;
    private final TestCaseTemplateRepository testCaseTemplateRepository;
    private final ProjectMapper projectMapper;

    @Override
    public ProjectDo execute(String name, UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UUID workspaceId = user.getLastActiveWorkspaceId();
        if (workspaceId == null) {
            Workspace ws = workspaceRepository.findFirstByOwnerUser_UserId(userId)
                    .orElseThrow(() -> new RuntimeException("No workspace found for user"));
            workspaceId = ws.getWorkspaceId();
        }

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new RuntimeException("Workspace not found"));

        String projectKey = generateUniqueProjectKey(name, workspace.getWorkspaceId());

        Project projectEntity = Project
                .builder()
                .name(name)
                .projectKey(projectKey)
                .workspace(workspace)
                .createdByUser(user)
                .status("ACTIVE")
                .build();

        var savedEntity = projectRepository.save(projectEntity);
        return projectMapper.toDomain(savedEntity);
    }

    private String generateUniqueProjectKey(String name, UUID workspaceId) {
        String baseKey = generateProjectKey(name);
        String uniqueKey = baseKey;
        int count = 1;

        while (projectRepository.existsByProjectKeyAndWorkspace_WorkspaceId(uniqueKey, workspaceId)) {
            uniqueKey = baseKey + count;
            count++;
        }

        return uniqueKey;
    }

    @Override
    public ProjectDo execute(UUID projectId, String description, List<BusinessRule> businessRules) {
        var projectEntity = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        projectEntity.setDescription(description);

        if (projectEntity.getBusinessRules() == null) {
            projectEntity.setBusinessRules(new ArrayList<>());
        }
        projectEntity.getBusinessRules().clear();

        if (businessRules != null) {
            for (BusinessRule br : businessRules) {
                projectEntity.getBusinessRules()
                        .add(com.group05.TC_LLM_Generator.infrastructure.persistence.entity.BusinessRule.builder()
                                .project(projectEntity)
                                .title(br.getTitle())
                                .description(br.getDescription())
                                .priority(br.getPriority())
                                .build());
            }
        }

        assignDefaultTemplate(projectEntity);

        var savedEntity = projectRepository.save(projectEntity);
        return projectMapper.toDomain(savedEntity);
    }

    private void assignDefaultTemplate(Project project) {
        boolean hasDefault = !testCaseTemplateRepository.findByProject_ProjectIdAndIsDefaultTrue(project.getProjectId())
                .isEmpty();
        if (!hasDefault) {
            TestCaseTemplate template = TestCaseTemplate.builder()
                    .project(project)
                    .name("Default Template")
                    .description("Standard test case template")
                    .isDefault(true)
                    .build();
            testCaseTemplateRepository.save(template);
        }
    }

    private String generateProjectKey(String name) {
        if (name == null || name.isEmpty())
            return "PRJ";
        String cleaned = name.replaceAll("[^a-zA-Z0-9\\s]", "");
        String[] words = cleaned.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        if (words.length == 1) {
            String word = words[0];
            sb.append(word.substring(0, Math.min(word.length(), 3)).toUpperCase());
        } else {
            for (String word : words) {
                if (!word.isEmpty()) {
                    sb.append(Character.toUpperCase(word.charAt(0)));
                }
            }
        }

        String key = sb.toString();
        if (key.length() < 2) {
            key = (key + "X").toUpperCase();
        }
        return key;
    }
}
