package com.group05.TC_LLM_Generator.application.port.out;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestCaseTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TestCaseTemplateRepositoryPort {

    TestCaseTemplate save(TestCaseTemplate template);

    Optional<TestCaseTemplate> findById(UUID templateId);

    List<TestCaseTemplate> findByProjectId(UUID projectId);

    List<TestCaseTemplate> findDefaultByProjectId(UUID projectId);

    void deleteById(UUID templateId);

    boolean existsById(UUID templateId);
}
