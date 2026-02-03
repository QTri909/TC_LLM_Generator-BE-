package com.group05.TC_LLM_Generator.infrastructure.persistence.repository;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.PlanSuite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for PlanSuite entity
 */
@Repository
public interface PlanSuiteRepository extends JpaRepository<PlanSuite, UUID> {

    /**
     * Find plan suites by test plan ID
     * @param testPlanId test plan ID
     * @return List of plan suites
     */
    List<PlanSuite> findByTestPlan_TestPlanId(UUID testPlanId);

    /**
     * Find plan suites by test suite ID
     * @param testSuiteId test suite ID
     * @return List of plan suites
     */
    List<PlanSuite> findByTestSuite_TestSuiteId(UUID testSuiteId);

    /**
     * Find plan suites ordered by display order
     * @param testPlanId test plan ID
     * @return List of plan suites ordered by display order
     */
    List<PlanSuite> findByTestPlan_TestPlanIdOrderByDisplayOrderAsc(UUID testPlanId);
}
