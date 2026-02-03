package com.group05.TC_LLM_Generator.infrastructure.persistence.repository;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestCaseTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for TestCaseTemplate entity
 */
@Repository
public interface TestCaseTemplateRepository extends JpaRepository<TestCaseTemplate, UUID> {

    /**
     * Find test case templates by project ID
     * @param projectId project ID
     * @return List of test case templates
     */
    List<TestCaseTemplate> findByProject_ProjectId(UUID projectId);

    /**
     * Find default test case templates for a project
     * @param projectId project ID
     * @return List of default test case templates
     */
    List<TestCaseTemplate> findByProject_ProjectIdAndIsDefaultTrue(UUID projectId);

    /**
     * Find test case template by project ID and name
     * @param projectId project ID
     * @param name template name
     * @return Optional of TestCaseTemplate
     */
    Optional<TestCaseTemplate> findByProject_ProjectIdAndName(UUID projectId, String name);
}
