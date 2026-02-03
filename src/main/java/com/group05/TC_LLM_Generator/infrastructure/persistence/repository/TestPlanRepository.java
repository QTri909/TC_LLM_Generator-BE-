package com.group05.TC_LLM_Generator.infrastructure.persistence.repository;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Repository interface for TestPlan entity
 */
@Repository
public interface TestPlanRepository extends JpaRepository<TestPlan, UUID> {

    /**
     * Find test plans by project ID
     * @param projectId project ID
     * @return List of test plans
     */
    List<TestPlan> findByProject_ProjectId(UUID projectId);

    /**
     * Find test plans by status
     * @param projectId project ID
     * @param status test plan status
     * @return List of test plans with the specified status
     */
    List<TestPlan> findByProject_ProjectIdAndStatus(UUID projectId, String status);

    /**
     * Find test plans ordered by creation date
     * @param projectId project ID
     * @return List of test plans ordered by creation date
     */
    List<TestPlan> findByProject_ProjectIdOrderByCreatedAtDesc(UUID projectId);

    /**
     * Find test plans by created by user ID
     * @param userId user ID
     * @return List of test plans created by the user
     */
    List<TestPlan> findByCreatedByUser_UserId(UUID userId);

    /**
     * Find test plans by name containing (case-insensitive search)
     * @param projectId project ID
     * @param name search term
     * @return List of matching test plans
     */
    List<TestPlan> findByProject_ProjectIdAndNameContainingIgnoreCase(UUID projectId, String name);
}
