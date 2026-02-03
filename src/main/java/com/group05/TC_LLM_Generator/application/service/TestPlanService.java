package com.group05.TC_LLM_Generator.application.service;

import com.group05.TC_LLM_Generator.application.port.out.TestPlanRepositoryPort;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestPlan;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Application Service for TestPlan entity
 * Handles CRUD operations and test plan-related use cases
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TestPlanService {

    private final TestPlanRepositoryPort testPlanRepository;

    /**
     * Create a new test plan
     */
    @Transactional
    public TestPlan createTestPlan(TestPlan testPlan) {
        return testPlanRepository.save(testPlan);
    }

    /**
     * Get test plan by ID
     */
    public Optional<TestPlan> getTestPlanById(UUID testPlanId) {
        return testPlanRepository.findById(testPlanId);
    }

    /**
     * Get all test plans
     */
    public List<TestPlan> getAllTestPlans() {
        return testPlanRepository.findAll();
    }

    /**
     * Get test plans by project ID
     */
    public List<TestPlan> getTestPlansByProject(UUID projectId) {
        return testPlanRepository.findByProjectId(projectId);
    }

    /**
     * Update test plan
     */
    @Transactional
    public TestPlan updateTestPlan(UUID testPlanId, TestPlan updatedTestPlan) {
        TestPlan existingTestPlan = testPlanRepository.findById(testPlanId)
                .orElseThrow(() -> new IllegalArgumentException("Test plan not found: " + testPlanId));

        // Update fields
        if (updatedTestPlan.getName() != null) {
            existingTestPlan.setName(updatedTestPlan.getName());
        }
        
        if (updatedTestPlan.getDescription() != null) {
            existingTestPlan.setDescription(updatedTestPlan.getDescription());
        }

        return testPlanRepository.save(existingTestPlan);
    }

    /**
     * Delete test plan by ID
     */
    @Transactional
    public void deleteTestPlan(UUID testPlanId) {
        if (!testPlanRepository.existsById(testPlanId)) {
            throw new IllegalArgumentException("Test plan not found: " + testPlanId);
        }
        testPlanRepository.deleteById(testPlanId);
    }

    /**
     * Check if test plan exists
     */
    public boolean testPlanExists(UUID testPlanId) {
        return testPlanRepository.existsById(testPlanId);
    }
}
