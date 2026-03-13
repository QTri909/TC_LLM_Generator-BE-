package com.group05.TC_LLM_Generator.application.port.out;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.PlanSuite;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlanSuiteRepositoryPort {

    PlanSuite save(PlanSuite planSuite);

    List<PlanSuite> findByTestPlanId(UUID testPlanId);

    List<PlanSuite> findByTestPlanIdOrdered(UUID testPlanId);

    Optional<PlanSuite> findByTestPlanIdAndTestSuiteId(UUID testPlanId, UUID testSuiteId);

    void deleteByTestPlanIdAndTestSuiteId(UUID testPlanId, UUID testSuiteId);

    long countByTestPlanId(UUID testPlanId);
}
