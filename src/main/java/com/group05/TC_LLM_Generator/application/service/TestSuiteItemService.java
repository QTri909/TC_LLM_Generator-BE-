package com.group05.TC_LLM_Generator.application.service;

import com.group05.TC_LLM_Generator.application.port.out.TestSuiteItemRepositoryPort;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestCase;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestSuite;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestSuiteItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TestSuiteItemService {

    private final TestSuiteItemRepositoryPort testSuiteItemRepository;

    @Transactional
    public TestSuiteItem addTestCaseToSuite(TestSuite testSuite, TestCase testCase) {
        // Check if already exists
        if (testSuiteItemRepository.findByTestSuiteIdAndTestCaseId(
                testSuite.getTestSuiteId(), testCase.getTestCaseId()).isPresent()) {
            throw new IllegalArgumentException("Test case already exists in this suite");
        }

        int nextOrder = (int) testSuiteItemRepository.countByTestSuiteId(testSuite.getTestSuiteId()) + 1;

        TestSuiteItem item = TestSuiteItem.builder()
                .testSuite(testSuite)
                .testCase(testCase)
                .displayOrder(nextOrder)
                .build();

        return testSuiteItemRepository.save(item);
    }

    @Transactional
    public void removeTestCaseFromSuite(UUID testSuiteId, UUID testCaseId) {
        testSuiteItemRepository.findByTestSuiteIdAndTestCaseId(testSuiteId, testCaseId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Test case " + testCaseId + " not found in suite " + testSuiteId));
        testSuiteItemRepository.deleteByTestSuiteIdAndTestCaseId(testSuiteId, testCaseId);
    }

    public List<TestSuiteItem> getTestCasesInSuite(UUID testSuiteId) {
        return testSuiteItemRepository.findByTestSuiteIdOrdered(testSuiteId);
    }
}
