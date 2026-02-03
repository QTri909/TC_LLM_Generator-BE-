package com.group05.TC_LLM_Generator.infrastructure.persistence.repository;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestPlanItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for TestPlanItem entity
 */
@Repository
public interface TestPlanItemRepository extends JpaRepository<TestPlanItem, UUID> {

    /**
     * Find test plan items by test plan ID
     * @param testPlanId test plan ID
     * @return List of test plan items
     */
    List<TestPlanItem> findByTestPlan_TestPlanId(UUID testPlanId);

    /**
     * Find test plan items by test case ID
     * @param testCaseId test case ID
     * @return List of test plan items
     */
    List<TestPlanItem> findByTestCase_TestCaseId(UUID testCaseId);

    /**
     * Find test plan items by status
     * @param testPlanId test plan ID
     * @param status execution status
     * @return List of test plan items with the specified status
     */
    List<TestPlanItem> findByTestPlan_TestPlanIdAndStatus(UUID testPlanId, String status);

    /**
     * Find test plan items by executed by user ID
     * @param userId user ID
     * @return List of test plan items executed by the user
     */
    List<TestPlanItem> findByExecutedByUser_UserId(UUID userId);

    /**
     * Find test plan items ordered by start date
     * @param testPlanId test plan ID
     * @return List of test plan items ordered by start date
     */
    List<TestPlanItem> findByTestPlan_TestPlanIdOrderByStartedAtDesc(UUID testPlanId);

    /**
     * Find test plan items by test plan ID and test case ID
     * @param testPlanId test plan ID
     * @param testCaseId test case ID
     * @return List of test plan items
     */
    List<TestPlanItem> findByTestPlan_TestPlanIdAndTestCase_TestCaseId(UUID testPlanId, UUID testCaseId);
}
