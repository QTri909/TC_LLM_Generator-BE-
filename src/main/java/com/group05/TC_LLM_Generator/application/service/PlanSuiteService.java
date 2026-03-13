package com.group05.TC_LLM_Generator.application.service;

import com.group05.TC_LLM_Generator.application.port.out.PlanSuiteRepositoryPort;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.PlanSuite;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestPlan;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestSuite;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlanSuiteService {

    private final PlanSuiteRepositoryPort planSuiteRepository;

    @Transactional
    public PlanSuite attachSuiteToPlan(TestPlan testPlan, TestSuite testSuite) {
        // Check if already attached
        if (planSuiteRepository.findByTestPlanIdAndTestSuiteId(
                testPlan.getTestPlanId(), testSuite.getTestSuiteId()).isPresent()) {
            throw new IllegalArgumentException("Test suite already attached to this plan");
        }

        int nextOrder = (int) planSuiteRepository.countByTestPlanId(testPlan.getTestPlanId()) + 1;

        PlanSuite planSuite = PlanSuite.builder()
                .testPlan(testPlan)
                .testSuite(testSuite)
                .displayOrder(nextOrder)
                .build();

        return planSuiteRepository.save(planSuite);
    }

    @Transactional
    public void detachSuiteFromPlan(UUID testPlanId, UUID testSuiteId) {
        planSuiteRepository.findByTestPlanIdAndTestSuiteId(testPlanId, testSuiteId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Test suite " + testSuiteId + " not attached to plan " + testPlanId));
        planSuiteRepository.deleteByTestPlanIdAndTestSuiteId(testPlanId, testSuiteId);
    }

    public List<PlanSuite> getSuitesInPlan(UUID testPlanId) {
        return planSuiteRepository.findByTestPlanIdOrdered(testPlanId);
    }
}
