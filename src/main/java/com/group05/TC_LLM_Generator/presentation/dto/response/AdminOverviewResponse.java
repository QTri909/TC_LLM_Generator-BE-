package com.group05.TC_LLM_Generator.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Response DTO for admin overview / platform dashboard
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminOverviewResponse {

    // Stats cards
    private long totalUsers;
    private long activeUsers;
    private long totalWorkspaces;
    private long totalProjects;
    private long totalTestCases;

    // User growth over last 30 days (for line chart)
    private List<DailyCount> userGrowth;

    // User role distribution (for doughnut chart)
    private Map<String, Long> roleDistribution;

    // Recent system activity
    private List<RecentActivity> recentActivities;

    // Top workspaces by project count
    private List<TopWorkspace> topWorkspaces;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyCount {
        private String date;  // "2026-03-01"
        private long count;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentActivity {
        private String event;
        private String userName;
        private String userEmail;
        private Instant timestamp;
        private String status;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopWorkspace {
        private String workspaceId;
        private String name;
        private long memberCount;
        private long projectCount;
    }
}
