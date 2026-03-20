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
     * Get test case type distribution for a specific project.
     */
    public Map<String, Long> getTestCaseTypeDistribution(UUID projectId) {
        List<TestCase> cases = testCaseRepository.findByProjectId(projectId);
        return cases.stream()
                .collect(Collectors.groupingBy(
                        tc -> tc.getTestCaseType() != null ? tc.getTestCaseType().getName() : "Uncategorized",
                        Collectors.counting()
                ));
    }

    /**
     * Get AI vs Manual generation breakdown for a specific project.
     */
    public Map<String, Long> getAiVsManualDistribution(UUID projectId) {
        List<TestCase> cases = testCaseRepository.findByProjectId(projectId);
        long aiGenerated = cases.stream().filter(tc -> Boolean.TRUE.equals(tc.getGeneratedByAi())).count();
        long manual = cases.size() - aiGenerated;

        Map<String, Long> result = new LinkedHashMap<>();
        if (aiGenerated > 0) result.put("AI Generated", aiGenerated);
        if (manual > 0) result.put("Manual", manual);
        return result;
    }

    /**
     * Get user story status distribution for a specific project.
     */
    public Map<String, Long> getStoryStatusDistribution(UUID projectId) {
        List<UserStory> stories = userStoryRepository.findByProjectId(projectId);
        return stories.stream()
                .collect(Collectors.groupingBy(
                        s -> s.getStatus() != null ? s.getStatus().name() : "UNKNOWN",
                        Collectors.counting()
                ));
    }

    /**
     * Get requirement coverage for a specific project.
     */
    public Map<String, Object> getRequirementCoverage(UUID projectId) {
        List<UserStory> stories = userStoryRepository.findByProjectId(projectId);
        List<TestCase> cases = testCaseRepository.findByProjectId(projectId);

        Set<UUID> coveredStoryIds = cases.stream()
                .filter(tc -> tc.getUserStory() != null)
                .map(tc -> tc.getUserStory().getUserStoryId())
                .collect(Collectors.toSet());

        long totalStories = stories.size();
        long covered = stories.stream()
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
     * Aggregate all report data for a specific project.
     */
    public Map<String, Object> getFullReport(UUID projectId) {
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("testCaseTypeDistribution", getTestCaseTypeDistribution(projectId));
        report.put("aiVsManualDistribution", getAiVsManualDistribution(projectId));
        report.put("storyStatusDistribution", getStoryStatusDistribution(projectId));
        report.put("requirementCoverage", getRequirementCoverage(projectId));
        return report;
    }
}
