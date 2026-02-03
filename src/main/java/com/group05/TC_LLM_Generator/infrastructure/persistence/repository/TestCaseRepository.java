package com.group05.TC_LLM_Generator.infrastructure.persistence.repository;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for TestCase entity
 */
@Repository
public interface TestCaseRepository extends JpaRepository<TestCase, UUID> {

    /**
     * Find test cases by acceptance criteria ID
     * @param acceptanceCriteriaId acceptance criteria ID
     * @return List of test cases
     */
    List<TestCase> findByAcceptanceCriteria_AcceptanceCriteriaId(UUID acceptanceCriteriaId);

    /**
     * Find test cases by test case type ID
     * @param testCaseTypeId test case type ID
     * @return List of test cases
     */
    List<TestCase> findByTestCaseType_TestCaseTypeId(UUID testCaseTypeId);

    /**
     * Find AI-generated test cases
     * @param acceptanceCriteriaId acceptance criteria ID
     * @return List of AI-generated test cases
     */
    List<TestCase> findByAcceptanceCriteria_AcceptanceCriteriaIdAndGeneratedByAiTrue(UUID acceptanceCriteriaId);

    /**
     * Find manually created test cases
     * @param acceptanceCriteriaId acceptance criteria ID
     * @return List of manually created test cases
     */
    List<TestCase> findByAcceptanceCriteria_AcceptanceCriteriaIdAndGeneratedByAiFalse(UUID acceptanceCriteriaId);

    /**
     * Find test cases by title containing (case-insensitive search)
     * @param title search term
     * @return List of matching test cases
     */
    List<TestCase> findByTitleContainingIgnoreCase(String title);
}
