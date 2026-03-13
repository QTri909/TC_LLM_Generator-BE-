package com.group05.TC_LLM_Generator.infrastructure.persistence.repository;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for TestCase entity
 */
@Repository
public interface TestCaseRepository extends JpaRepository<TestCase, UUID> {

    List<TestCase> findByUserStory_UserStoryId(UUID userStoryId);

    Page<TestCase> findByUserStory_UserStoryId(UUID userStoryId, Pageable pageable);

    List<TestCase> findByAcceptanceCriteria_AcceptanceCriteriaId(UUID acceptanceCriteriaId);

    Page<TestCase> findByAcceptanceCriteria_AcceptanceCriteriaId(UUID acceptanceCriteriaId, Pageable pageable);

    List<TestCase> findByTestCaseType_TestCaseTypeId(UUID testCaseTypeId);

    Page<TestCase> findByTestCaseType_TestCaseTypeId(UUID testCaseTypeId, Pageable pageable);

    List<TestCase> findByAcceptanceCriteria_AcceptanceCriteriaIdAndGeneratedByAiTrue(UUID acceptanceCriteriaId);

    List<TestCase> findByAcceptanceCriteria_AcceptanceCriteriaIdAndGeneratedByAiFalse(UUID acceptanceCriteriaId);

    List<TestCase> findByTitleContainingIgnoreCase(String title);

    Page<TestCase> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
