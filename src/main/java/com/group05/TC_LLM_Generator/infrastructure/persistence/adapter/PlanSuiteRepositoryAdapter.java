package com.group05.TC_LLM_Generator.infrastructure.persistence.adapter;

import com.group05.TC_LLM_Generator.application.port.out.PlanSuiteRepositoryPort;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.PlanSuite;
import com.group05.TC_LLM_Generator.infrastructure.persistence.repository.PlanSuiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PlanSuiteRepositoryAdapter implements PlanSuiteRepositoryPort {

    private final PlanSuiteRepository jpaRepository;

    @Override
    public PlanSuite save(PlanSuite planSuite) {
        return jpaRepository.save(planSuite);
    }

    @Override
    public List<PlanSuite> findByTestPlanId(UUID testPlanId) {
        return jpaRepository.findByTestPlan_TestPlanId(testPlanId);
    }

    @Override
    public List<PlanSuite> findByTestPlanIdOrdered(UUID testPlanId) {
        return jpaRepository.findByTestPlan_TestPlanIdOrderByDisplayOrderAsc(testPlanId);
    }

    @Override
    public Optional<PlanSuite> findByTestPlanIdAndTestSuiteId(UUID testPlanId, UUID testSuiteId) {
        return jpaRepository.findByTestPlan_TestPlanIdAndTestSuite_TestSuiteId(testPlanId, testSuiteId);
    }

    @Override
    public void deleteByTestPlanIdAndTestSuiteId(UUID testPlanId, UUID testSuiteId) {
        jpaRepository.deleteByTestPlan_TestPlanIdAndTestSuite_TestSuiteId(testPlanId, testSuiteId);
    }

    @Override
    public long countByTestPlanId(UUID testPlanId) {
        return jpaRepository.countByTestPlan_TestPlanId(testPlanId);
    }
}
