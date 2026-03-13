package com.group05.TC_LLM_Generator.application.port.out;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestSuiteItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TestSuiteItemRepositoryPort {

    TestSuiteItem save(TestSuiteItem item);

    List<TestSuiteItem> findByTestSuiteId(UUID testSuiteId);

    List<TestSuiteItem> findByTestSuiteIdOrdered(UUID testSuiteId);

    Optional<TestSuiteItem> findByTestSuiteIdAndTestCaseId(UUID testSuiteId, UUID testCaseId);

    void deleteByTestSuiteIdAndTestCaseId(UUID testSuiteId, UUID testCaseId);

    long countByTestSuiteId(UUID testSuiteId);
}
