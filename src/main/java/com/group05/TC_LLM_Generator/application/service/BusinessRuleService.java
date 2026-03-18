package com.group05.TC_LLM_Generator.application.service;

import com.group05.TC_LLM_Generator.application.port.out.BusinessRuleRepositoryPort;
import com.group05.TC_LLM_Generator.application.port.out.ProjectRepositoryPort;
import com.group05.TC_LLM_Generator.application.port.out.UserStoryRepositoryPort;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.BusinessRule;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.Project;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserStory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BusinessRuleService {

    private final BusinessRuleRepositoryPort businessRuleRepository;
    private final ProjectRepositoryPort projectRepository;
    private final UserStoryRepositoryPort userStoryRepository;

    @Transactional
    public BusinessRule createBusinessRule(UUID projectId, String title, String description,
                                           Integer priority, String source, UUID userStoryId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));

        BusinessRule rule = BusinessRule.builder()
                .project(project)
                .title(title)
                .description(description)
                .priority(priority != null ? priority : 0)
                .source(source)
                .build();

        if (userStoryId != null) {
            UserStory story = userStoryRepository.findById(userStoryId)
                    .orElseThrow(() -> new IllegalArgumentException("User story not found: " + userStoryId));
            rule.setUserStory(story);
        }

        return businessRuleRepository.save(rule);
    }

    public Page<BusinessRule> getByProject(UUID projectId, Pageable pageable) {
        return businessRuleRepository.findByProjectId(projectId, pageable);
    }

    public List<BusinessRule> getByUserStory(UUID userStoryId) {
        return businessRuleRepository.findByUserStoryId(userStoryId);
    }

    public Optional<BusinessRule> getById(UUID businessRuleId) {
        return businessRuleRepository.findById(businessRuleId);
    }

    @Transactional
    public BusinessRule updateBusinessRule(UUID businessRuleId, String title, String description,
                                           Integer priority, String source, UUID userStoryId) {
        BusinessRule existing = businessRuleRepository.findById(businessRuleId)
                .orElseThrow(() -> new IllegalArgumentException("Business rule not found: " + businessRuleId));

        if (title != null) existing.setTitle(title);
        if (description != null) existing.setDescription(description);
        if (priority != null) existing.setPriority(priority);
        if (source != null) existing.setSource(source);

        if (userStoryId != null) {
            UserStory story = userStoryRepository.findById(userStoryId)
                    .orElseThrow(() -> new IllegalArgumentException("User story not found: " + userStoryId));
            existing.setUserStory(story);
        }

        return businessRuleRepository.save(existing);
    }

    @Transactional
    public void deleteBusinessRule(UUID businessRuleId) {
        if (!businessRuleRepository.existsById(businessRuleId)) {
            throw new IllegalArgumentException("Business rule not found: " + businessRuleId);
        }
        businessRuleRepository.deleteById(businessRuleId);
    }

    public long countByProject(UUID projectId) {
        return businessRuleRepository.countByProjectId(projectId);
    }
}
