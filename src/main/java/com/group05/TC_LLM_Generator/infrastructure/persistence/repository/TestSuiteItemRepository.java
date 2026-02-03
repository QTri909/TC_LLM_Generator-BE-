package com.group05.TC_LLM_Generator.infrastructure.persistence.repository;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestSuiteItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for TestSuiteItem entity
 */
@Repository
public interface TestSuiteItemRepository extends JpaRepository<TestSuiteItem, UUID> {

    /**
     * Find test suite items by test suite ID
     * @param testSuiteId test suite ID
     * @return List of test suite items
     */
    List<TestSuiteItem> findByTestSuite_TestSuiteId(UUID testSuiteId);

    /**
     * Find test suite items by test case ID
     * @param testCaseId test case ID
     * @return List of test suite items
     */
    List<TestSuiteItem> findByTestCase_TestCaseId(UUID testCaseId);

    /**
     * Find test suite items ordered by display order
     * @param testSuiteId test suite ID
     * @return List of test suite items ordered by display order
     */
    List<TestSuiteItem> findByTestSuite_TestSuiteIdOrderByDisplayOrderAsc(UUID testSuiteId);
}
