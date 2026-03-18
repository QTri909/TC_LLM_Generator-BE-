package com.group05.TC_LLM_Generator.application.service;

import com.group05.TC_LLM_Generator.application.port.out.ProjectRepositoryPort;
import com.group05.TC_LLM_Generator.application.port.out.TemplateFieldRepositoryPort;
import com.group05.TC_LLM_Generator.application.port.out.TestCaseTemplateRepositoryPort;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.Project;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TemplateField;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestCaseTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TestCaseTemplateService {

    private final TestCaseTemplateRepositoryPort templateRepository;
    private final TemplateFieldRepositoryPort fieldRepository;
    private final ProjectRepositoryPort projectRepository;

    @Transactional
    public TestCaseTemplate createTemplate(UUID projectId, String name, String description, Boolean isDefault) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));

        TestCaseTemplate template = TestCaseTemplate.builder()
                .project(project)
                .name(name)
                .description(description)
                .isDefault(isDefault != null ? isDefault : false)
                .build();

        return templateRepository.save(template);
    }

    public List<TestCaseTemplate> getByProject(UUID projectId) {
        return templateRepository.findByProjectId(projectId);
    }

    public Optional<TestCaseTemplate> getById(UUID templateId) {
        return templateRepository.findById(templateId);
    }

    public List<TemplateField> getFieldsByTemplate(UUID templateId) {
        return fieldRepository.findByTemplateIdOrdered(templateId);
    }

    @Transactional
    public TestCaseTemplate updateTemplate(UUID templateId, String name, String description, Boolean isDefault) {
        TestCaseTemplate existing = templateRepository.findById(templateId)
                .orElseThrow(() -> new IllegalArgumentException("Template not found: " + templateId));

        if (name != null) existing.setName(name);
        if (description != null) existing.setDescription(description);
        if (isDefault != null) existing.setIsDefault(isDefault);

        return templateRepository.save(existing);
    }

    @Transactional
    public void deleteTemplate(UUID templateId) {
        TestCaseTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new IllegalArgumentException("Template not found: " + templateId));

        if (Boolean.TRUE.equals(template.getIsDefault())) {
            throw new IllegalArgumentException("Cannot delete the default template");
        }

        fieldRepository.deleteByTemplateId(templateId);
        templateRepository.deleteById(templateId);
    }

    @Transactional
    public List<TemplateField> replaceFields(UUID templateId, List<TemplateField> newFields) {
        TestCaseTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new IllegalArgumentException("Template not found: " + templateId));

        fieldRepository.deleteByTemplateId(templateId);

        for (int i = 0; i < newFields.size(); i++) {
            TemplateField f = newFields.get(i);
            f.setTestCaseTemplate(template);
            f.setDisplayOrder(i);
        }

        return fieldRepository.saveAll(newFields);
    }
}
