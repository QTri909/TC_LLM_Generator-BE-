package com.group05.TC_LLM_Generator.application.service;

import com.group05.TC_LLM_Generator.application.port.out.TestCaseRepositoryPort;
import com.group05.TC_LLM_Generator.application.port.out.UserStoryRepositoryPort;
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

    /**
     * Get test case type distribution (by TestCaseType name).
     */
    public Map<String, Long> getTestCaseTypeDistribution() {
        List<TestCase> allCases = testCaseRepository.findAll();
        return allCases.stream()
                .collect(Collectors.groupingBy(
                        tc -> tc.getTestCaseType() != null ? tc.getTestCaseType().getName() : "Uncategorized",
                        Collectors.counting()
                ));
    }

    /**
     * Get AI vs Manual generation breakdown.
     */
    public Map<String, Long> getAiVsManualDistribution() {
        List<TestCase> allCases = testCaseRepository.findAll();
        long aiGenerated = allCases.stream().filter(tc -> Boolean.TRUE.equals(tc.getGeneratedByAi())).count();
        long manual = allCases.size() - aiGenerated;

        Map<String, Long> result = new LinkedHashMap<>();
        if (aiGenerated > 0) result.put("AI Generated", aiGenerated);
        if (manual > 0) result.put("Manual", manual);
        return result;
    }

    /**
     * Get user story status distribution.
     */
    public Map<String, Long> getStoryStatusDistribution() {
        List<UserStory> allStories = userStoryRepository.findAll();
        return allStories.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getStatus() != null ? s.getStatus().name() : "UNKNOWN",
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

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalStories", totalStories);
        result.put("coveredStories", covered);
        result.put("uncoveredStories", totalStories - covered);
        result.put("coveragePercent", totalStories > 0 ? Math.round(covered * 100.0 / totalStories) : 0);
        return result;
    }

    /**
     * Aggregate all report data.
     */
    public Map<String, Object> getFullReport() {
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("testCaseTypeDistribution", getTestCaseTypeDistribution());
        report.put("aiVsManualDistribution", getAiVsManualDistribution());
        report.put("storyStatusDistribution", getStoryStatusDistribution());
        report.put("requirementCoverage", getRequirementCoverage());
        return report;
    }
}
