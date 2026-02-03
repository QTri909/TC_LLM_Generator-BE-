package com.group05.TC_LLM_Generator.infrastructure.persistence.repository;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestCaseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for TestCaseType entity
 */
@Repository
public interface TestCaseTypeRepository extends JpaRepository<TestCaseType, UUID> {

    /**
     * Find test case types by project ID
     * @param projectId project ID
     * @return List of test case types
     */
    List<TestCaseType> findByProject_ProjectId(UUID projectId);

    /**
     * Find test case type by project ID and name
     * @param projectId project ID
     * @param name type name
     * @return Optional of TestCaseType
     */
    Optional<TestCaseType> findByProject_ProjectIdAndName(UUID projectId, String name);

    /**
     * Find default test case types for a project
     * @param projectId project ID
     * @return List of default test case types
     */
    List<TestCaseType> findByProject_ProjectIdAndIsDefaultTrue(UUID projectId);
}
