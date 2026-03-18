package com.group05.TC_LLM_Generator.infrastructure.persistence.adapter;

import com.group05.TC_LLM_Generator.application.port.out.TestCaseTemplateRepositoryPort;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestCaseTemplate;
import com.group05.TC_LLM_Generator.infrastructure.persistence.repository.TestCaseTemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TestCaseTemplateRepositoryAdapter implements TestCaseTemplateRepositoryPort {

    private final TestCaseTemplateRepository jpaRepository;

    @Override
    public TestCaseTemplate save(TestCaseTemplate template) {
        return jpaRepository.save(template);
    }

    @Override
    public Optional<TestCaseTemplate> findById(UUID templateId) {
        return jpaRepository.findById(templateId);
    }

    @Override
    public List<TestCaseTemplate> findByProjectId(UUID projectId) {
        return jpaRepository.findByProject_ProjectId(projectId);
    }

    @Override
    public List<TestCaseTemplate> findDefaultByProjectId(UUID projectId) {
        return jpaRepository.findByProject_ProjectIdAndIsDefaultTrue(projectId);
    }

    @Override
    public void deleteById(UUID templateId) {
        jpaRepository.deleteById(templateId);
    }

    @Override
    public boolean existsById(UUID templateId) {
        return jpaRepository.existsById(templateId);
    }
}
