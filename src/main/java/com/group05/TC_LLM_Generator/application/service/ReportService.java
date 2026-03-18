package com.group05.TC_LLM_Generator.application.service;

import com.group05.TC_LLM_Generator.application.port.out.*;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestCase;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserStory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

    private final TestCaseRepositoryPort testCaseRepository;
    private final UserStoryRepositoryPort userStoryRepository;
    private final TestSuiteRepositoryPort testSuiteRepository;
    private final TestPlanRepositoryPort testPlanRepository;

    /**
     * Get test case status distribution across workspace.
     */
    public Map<String, Long> getTestCaseStatusDistribution() {
        List<TestCase> allCases = testCaseRepository.findAll();
        return allCases.stream()
                .collect(Collectors.groupingBy(
                        tc -> tc.getStatus() != null ? tc.getStatus() : "UNKNOWN",
                        Collectors.counting()
                ));
    }

    /**
     * Get user story status distribution across workspace.
     */
    public Map<String, Long> getStoryStatusDistribution() {
        List<UserStory> allStories = userStoryRepository.findAll();
        return allStories.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getStatus() != null ? s.getStatus() : "UNKNOWN",
                        Collectors.counting()
                ));
    }

    /**
     * Get requirement coverage: stories with test cases vs stories without.
     */
    public Map<String, Object> getRequirementCoverage() {
        List<UserStory> allStories = userStoryRepository.findAll();
        List<TestCase> allCases = testCaseRepository.findAll();

        Set<UUID> coveredStoryIds = allCases.stream()
                .filter(tc -> tc.getUserStory() != null)
                .map(tc -> tc.getUserStory().getUserStoryId())
                .collect(Collectors.toSet());

        long totalStories = allStories.size();
        long covered = allStories.stream()
                .filter(s -> coveredStoryIds.contains(s.getUserStoryId()))
                .count();
        long uncovered = totalStories - covered;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalStories", totalStories);
        result.put("coveredStories", covered);
        result.put("uncoveredStories", uncovered);
        result.put("coveragePercent", totalStories > 0 ? Math.round(covered * 100.0 / totalStories) : 0);
        return result;
    }

    /**
     * Get test case type distribution (AI-generated vs manual).
     */
    public Map<String, Long> getTestCaseTypeDistribution() {
        List<TestCase> allCases = testCaseRepository.findAll();
        return allCases.stream()
                .collect(Collectors.groupingBy(
                        tc -> tc.getTestCaseType() != null ? tc.getTestCaseType().name() : "MANUAL",
                        Collectors.counting()
                ));
    }

    /**
     * Get test case priority distribution.
     */
    public Map<String, Long> getTestCasePriorityDistribution() {
        List<TestCase> allCases = testCaseRepository.findAll();
        return allCases.stream()
                .collect(Collectors.groupingBy(
                        tc -> tc.getPriority() != null ? tc.getPriority() : "MEDIUM",
                        Collectors.counting()
                ));
    }

    /**
     * Aggregate all report data into a single response.
     */
    public Map<String, Object> getFullReport() {
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("testCaseStatusDistribution", getTestCaseStatusDistribution());
        report.put("storyStatusDistribution", getStoryStatusDistribution());
        report.put("requirementCoverage", getRequirementCoverage());
        report.put("testCaseTypeDistribution", getTestCaseTypeDistribution());
        report.put("testCasePriorityDistribution", getTestCasePriorityDistribution());
        return report;
    }
}
