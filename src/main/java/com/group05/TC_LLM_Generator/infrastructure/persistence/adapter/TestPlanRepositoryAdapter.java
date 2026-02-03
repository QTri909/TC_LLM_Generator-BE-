package com.group05.TC_LLM_Generator.infrastructure.persistence.adapter;

import com.group05.TC_LLM_Generator.application.port.out.TestPlanRepositoryPort;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestPlan;
import com.group05.TC_LLM_Generator.infrastructure.persistence.repository.TestPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adapter for TestPlan repository
 */
@Component
@RequiredArgsConstructor
public class TestPlanRepositoryAdapter implements TestPlanRepositoryPort {

    private final TestPlanRepository jpaRepository;

    @Override
    public TestPlan save(TestPlan testPlan) {
        return jpaRepository.save(testPlan);
    }

    @Override
    public Optional<TestPlan> findById(UUID testPlanId) {
        return jpaRepository.findById(testPlanId);
    }

    @Override
    public List<TestPlan> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public List<TestPlan> findByProjectId(UUID projectId) {
        return jpaRepository.findByProject_ProjectId(projectId);
    }

    @Override
    public void deleteById(UUID testPlanId) {
        jpaRepository.deleteById(testPlanId);
    }

    @Override
    public boolean existsById(UUID testPlanId) {
        return jpaRepository.existsById(testPlanId);
    }
}
