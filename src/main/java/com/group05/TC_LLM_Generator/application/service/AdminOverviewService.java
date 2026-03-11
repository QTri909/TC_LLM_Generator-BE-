package com.group05.TC_LLM_Generator.application.service;

import com.group05.TC_LLM_Generator.application.port.out.*;
import com.group05.TC_LLM_Generator.domain.model.enums.Role;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserEntity;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.Workspace;
import com.group05.TC_LLM_Generator.presentation.dto.response.AdminOverviewResponse;
import com.group05.TC_LLM_Generator.presentation.dto.response.AdminOverviewResponse.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for aggregating admin overview / platform dashboard data.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminOverviewService {

    private final UserRepositoryPort userRepository;
    private final WorkspaceRepositoryPort workspaceRepository;
    private final ProjectRepositoryPort projectRepository;
    private final TestCaseRepositoryPort testCaseRepository;

    public AdminOverviewResponse getOverview() {
        // Stats cards
        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByStatus("ACTIVE");
        long totalWorkspaces = workspaceRepository.count();
        long totalProjects = projectRepository.count();
        long totalTestCases = testCaseRepository.count();

        // User growth - last 30 days
        Instant thirtyDaysAgo = Instant.now().minus(30, ChronoUnit.DAYS);
        List<Object[]> rawGrowth = userRepository.countUsersGroupedByDay(thirtyDaysAgo);
        List<DailyCount> userGrowth = buildDailyCountList(rawGrowth, thirtyDaysAgo);

        // Role distribution
        Map<String, Long> roleDistribution = new LinkedHashMap<>();
        for (Role role : Role.values()) {
            roleDistribution.put(role.name(), userRepository.countByRole(role));
        }

        // Recent activity (latest 5 users created)
        List<UserEntity> recentUsers = userRepository.findRecentUsers(5);
        List<RecentActivity> recentActivities = recentUsers.stream()
                .map(u -> RecentActivity.builder()
                        .event("User Registered")
                        .userName(u.getFullName())
                        .userEmail(u.getEmail())
                        .timestamp(u.getCreatedAt())
                        .status("Success")
                        .build())
                .collect(Collectors.toList());

        // Top workspaces by project count
        Page<Workspace> allWorkspaces = workspaceRepository.findAll(
                PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "createdAt")));
        List<TopWorkspace> topWorkspaces = allWorkspaces.getContent().stream()
                .map(ws -> {
                    long projCount = projectRepository.countByWorkspaceId(ws.getWorkspaceId());
                    return TopWorkspace.builder()
                            .workspaceId(ws.getWorkspaceId().toString())
                            .name(ws.getName())
                            .memberCount(0) // Will be enhanced later if needed
                            .projectCount(projCount)
                            .build();
                })
                .sorted(Comparator.comparingLong(TopWorkspace::getProjectCount).reversed())
                .limit(5)
                .collect(Collectors.toList());

        return AdminOverviewResponse.builder()
                .totalUsers(totalUsers)
                .activeUsers(activeUsers)
                .totalWorkspaces(totalWorkspaces)
                .totalProjects(totalProjects)
                .totalTestCases(totalTestCases)
                .userGrowth(userGrowth)
                .roleDistribution(roleDistribution)
                .recentActivities(recentActivities)
                .topWorkspaces(topWorkspaces)
                .build();
    }

    /**
     * Builds a full 30-day list filling in zeros for days with no data.
     */
    private List<DailyCount> buildDailyCountList(List<Object[]> rawData, Instant since) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Map raw data by date string
        Map<String, Long> dataMap = new HashMap<>();
        for (Object[] row : rawData) {
            String dateStr;
            if (row[0] instanceof java.sql.Date) {
                dateStr = ((java.sql.Date) row[0]).toLocalDate().format(fmt);
            } else if (row[0] instanceof LocalDate) {
                dateStr = ((LocalDate) row[0]).format(fmt);
            } else {
                dateStr = row[0].toString();
            }
            long count = ((Number) row[1]).longValue();
            dataMap.put(dateStr, count);
        }

        // Fill the entire 30-day range
        List<DailyCount> result = new ArrayList<>();
        LocalDate startDate = since.atZone(ZoneOffset.UTC).toLocalDate();
        LocalDate endDate = LocalDate.now(ZoneOffset.UTC);

        for (LocalDate d = startDate; !d.isAfter(endDate); d = d.plusDays(1)) {
            String key = d.format(fmt);
            result.add(DailyCount.builder()
                    .date(key)
                    .count(dataMap.getOrDefault(key, 0L))
                    .build());
        }

        return result;
    }
}
