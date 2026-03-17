package com.group05.TC_LLM_Generator.presentation.controller;

import com.group05.TC_LLM_Generator.application.service.PlanSuiteService;
import com.group05.TC_LLM_Generator.application.service.ProjectAuthorizationService;
import com.group05.TC_LLM_Generator.application.service.ProjectService;
import com.group05.TC_LLM_Generator.application.service.TestPlanService;
import com.group05.TC_LLM_Generator.application.service.TestSuiteService;
import com.group05.TC_LLM_Generator.application.service.UserService;
import com.group05.TC_LLM_Generator.application.service.UserStoryService;
import com.group05.TC_LLM_Generator.domain.model.enums.TestPlanStatus;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.PlanSuite;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.Project;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestPlan;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestSuite;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserEntity;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserStory;
import com.group05.TC_LLM_Generator.presentation.assembler.TestPlanModelAssembler;
import com.group05.TC_LLM_Generator.presentation.assembler.TestSuiteModelAssembler;
import com.group05.TC_LLM_Generator.presentation.assembler.UserStoryModelAssembler;
import com.group05.TC_LLM_Generator.presentation.dto.common.ApiResponse;
import com.group05.TC_LLM_Generator.presentation.dto.request.AttachSuiteToPlanRequest;
import com.group05.TC_LLM_Generator.presentation.dto.request.CreateTestPlanRequest;
import com.group05.TC_LLM_Generator.presentation.dto.request.UpdateTestPlanRequest;
import com.group05.TC_LLM_Generator.presentation.dto.request.UpdateTestPlanStatusRequest;
import com.group05.TC_LLM_Generator.presentation.dto.response.TestPlanResponse;
import com.group05.TC_LLM_Generator.presentation.dto.response.TestSuiteResponse;
import com.group05.TC_LLM_Generator.presentation.dto.response.UserStoryResponse;
import com.group05.TC_LLM_Generator.presentation.exception.ResourceNotFoundException;
import com.group05.TC_LLM_Generator.presentation.mapper.TestPlanPresentationMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/test-plans")
@RequiredArgsConstructor
public class TestPlanController {

    private final TestPlanService testPlanService;
    private final PlanSuiteService planSuiteService;
    private final TestSuiteService testSuiteService;
    private final ProjectService projectService;
    private final ProjectAuthorizationService projectAuth;
    private final UserService userService;
    private final UserStoryService userStoryService;
    private final TestPlanPresentationMapper mapper;
    private final TestPlanModelAssembler assembler;
    private final TestSuiteModelAssembler testSuiteAssembler;
    private final UserStoryModelAssembler userStoryAssembler;
    private final PagedResourcesAssembler<TestPlan> pagedResourcesAssembler;
    private final PagedResourcesAssembler<UserStory> userStoryPagedAssembler;

    @PostMapping
    public ResponseEntity<ApiResponse<TestPlanResponse>> createTestPlan(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateTestPlanRequest request) {

        UUID currentUserId = UUID.fromString(jwt.getSubject());

        Project project = projectService.getProjectById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", request.getProjectId()));

        // Auth: require contributor access
        projectAuth.requireContributorAccess(
                project.getProjectId(), project.getWorkspace().getWorkspaceId(), currentUserId);

        UserEntity creator = userService.getUserById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));

        TestPlanStatus status = TestPlanStatus.DRAFT;
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            try {
                status = TestPlanStatus.valueOf(request.getStatus().toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }

        TestPlan testPlan = TestPlan.builder()
                .project(project)
                .createdByUser(creator)
                .name(request.getName())
                .description(request.getDescription())
                .status(status)
                .build();

        List<UserStory> stories = resolveStories(request.getStoryIds());

        TestPlan saved = testPlanService.createTestPlan(testPlan, stories, currentUserId.toString());

        // Attach suites if provided
        if (request.getSuiteIds() != null) {
            for (UUID suiteId : request.getSuiteIds()) {
                TestSuite suite = testSuiteService.getTestSuiteById(suiteId)
                        .orElseThrow(() -> new ResourceNotFoundException("TestSuite", "id", suiteId));
                planSuiteService.attachSuiteToPlan(saved, suite);
            }
        }

        TestPlanResponse response = assembler.toModel(saved);
        enrichWithSuiteIds(response, saved.getTestPlanId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Test plan created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TestPlanResponse>> getTestPlanById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") UUID id) {

        TestPlan testPlan = testPlanService.getTestPlanById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestPlan", "id", id));

        // Auth: require read access
        UUID currentUserId = UUID.fromString(jwt.getSubject());
        Project project = testPlan.getProject();
        projectAuth.requireProjectAccess(
                project.getProjectId(), project.getWorkspace().getWorkspaceId(), currentUserId);

        TestPlanResponse response = assembler.toModel(testPlan);
        enrichWithSuiteIds(response, id);
        return ResponseEntity.ok(ApiResponse.success(response, "Test plan retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedModel<TestPlanResponse>>> getAllTestPlans(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<TestPlan> page = testPlanService.getAllTestPlans(pageable);
        PagedModel<TestPlanResponse> pagedModel = pagedResourcesAssembler.toModel(page, assembler);
        pagedModel.getContent().forEach(r -> enrichWithSuiteIds(r, r.getTestPlanId()));

        return ResponseEntity.ok(ApiResponse.success(pagedModel, "Test plans retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TestPlanResponse>> updateTestPlan(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateTestPlanRequest request) {

        String currentUserId = jwt.getSubject();

        TestPlan existing = testPlanService.getTestPlanById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestPlan", "id", id));

        // Auth: require contributor access
        Project project = existing.getProject();
        projectAuth.requireContributorAccess(
                project.getProjectId(), project.getWorkspace().getWorkspaceId(),
                UUID.fromString(currentUserId));

        mapper.updateEntity(request, existing);

        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            try {
                existing.setStatus(TestPlanStatus.valueOf(request.getStatus().toUpperCase()));
            } catch (IllegalArgumentException ignored) {
            }
        }

        List<UserStory> newStories = request.getStoryIds() != null
                ? resolveStories(request.getStoryIds())
                : null;

        TestPlan updated = testPlanService.updateTestPlan(id, existing, newStories, currentUserId);
        TestPlanResponse response = assembler.toModel(updated);
        enrichWithSuiteIds(response, id);

        return ResponseEntity.ok(ApiResponse.success(response, "Test plan updated successfully"));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<TestPlanResponse>> updateTestPlanStatus(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateTestPlanStatusRequest request) {

        String currentUserId = jwt.getSubject();

        TestPlan existing = testPlanService.getTestPlanById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestPlan", "id", id));

        // Auth: require contributor access for status changes
        Project project = existing.getProject();
        projectAuth.requireContributorAccess(
                project.getProjectId(), project.getWorkspace().getWorkspaceId(),
                UUID.fromString(currentUserId));

        TestPlanStatus newStatus;
        try {
            newStatus = TestPlanStatus.valueOf(request.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid status: " + request.getStatus() + ". Allowed: DRAFT, COMPLETED");
        }

        TestPlanStatus currentStatus = existing.getStatus();

        // Same status — no-op
        if (currentStatus == newStatus) {
            TestPlanResponse response = assembler.toModel(existing);
            enrichWithSuiteIds(response, id);
            return ResponseEntity.ok(ApiResponse.success(response, "No status change needed"));
        }

        // Validate transitions
        if (currentStatus == TestPlanStatus.DRAFT && newStatus == TestPlanStatus.COMPLETED) {
            // DRAFT → COMPLETED: must have ≥1 suite attached
            List<PlanSuite> suites = planSuiteService.getSuitesInPlan(id);
            if (suites.isEmpty()) {
                throw new IllegalStateException(
                        "Cannot finalize test plan: at least 1 test suite must be attached");
            }
        } else if (currentStatus == TestPlanStatus.COMPLETED && newStatus == TestPlanStatus.DRAFT) {
            // COMPLETED → DRAFT: always allowed (reopen)
        } else {
            throw new IllegalStateException(
                    "Invalid transition: " + currentStatus + " → " + newStatus
                    + ". Allowed: DRAFT → COMPLETED, COMPLETED → DRAFT");
        }

        existing.setStatus(newStatus);
        TestPlan updated = testPlanService.updateTestPlan(id, existing, null, currentUserId);
        TestPlanResponse response = assembler.toModel(updated);
        enrichWithSuiteIds(response, id);

        return ResponseEntity.ok(ApiResponse.success(response, "Test plan status updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTestPlan(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") UUID id) {

        String currentUserId = jwt.getSubject();

        TestPlan testPlan = testPlanService.getTestPlanById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestPlan", "id", id));

        // Auth: require Lead access for deletion
        Project project = testPlan.getProject();
        projectAuth.requireLeadAccess(
                project.getProjectId(), project.getWorkspace().getWorkspaceId(),
                UUID.fromString(currentUserId));

        testPlanService.deleteTestPlan(id, currentUserId);
        return ResponseEntity.ok(ApiResponse.success("Test plan deleted successfully"));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<ApiResponse<PagedModel<TestPlanResponse>>> getTestPlansByProject(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("projectId") UUID projectId,
            @RequestParam(required = false) String status,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Project project = projectService.getProjectById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        // Auth: require read access
        UUID currentUserId = UUID.fromString(jwt.getSubject());
        projectAuth.requireProjectAccess(
                projectId, project.getWorkspace().getWorkspaceId(), currentUserId);

        Page<TestPlan> page;

        if (status != null && !status.isBlank()) {
            try {
                TestPlanStatus statusEnum = TestPlanStatus.valueOf(status.toUpperCase());
                page = testPlanService.getTestPlansByProjectAndStatus(projectId, statusEnum, pageable);
            } catch (IllegalArgumentException e) {
                page = testPlanService.getTestPlansByProject(projectId, pageable);
            }
        } else {
            page = testPlanService.getTestPlansByProject(projectId, pageable);
        }

        PagedModel<TestPlanResponse> pagedModel = pagedResourcesAssembler.toModel(page, assembler);
        pagedModel.getContent().forEach(r -> enrichWithSuiteIds(r, r.getTestPlanId()));
        return ResponseEntity.ok(ApiResponse.success(pagedModel, "Test plans retrieved successfully"));
    }

    @GetMapping("/{id}/stories")
    public ResponseEntity<ApiResponse<PagedModel<UserStoryResponse>>> getTestPlanStories(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") UUID id,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        TestPlan testPlan = testPlanService.getTestPlanById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestPlan", "id", id));

        // Auth: require read access
        UUID currentUserId = UUID.fromString(jwt.getSubject());
        Project project = testPlan.getProject();
        projectAuth.requireProjectAccess(
                project.getProjectId(), project.getWorkspace().getWorkspaceId(), currentUserId);

        Page<UserStory> page = testPlanService.getStoriesByTestPlanId(id, pageable);
        PagedModel<UserStoryResponse> pagedModel = userStoryPagedAssembler.toModel(page, userStoryAssembler);

        return ResponseEntity.ok(ApiResponse.success(pagedModel, "Test plan stories retrieved successfully"));
    }

    // ---- PlanSuite endpoints: manage test suites in a plan ----

    @PostMapping("/{planId}/test-suites")
    public ResponseEntity<ApiResponse<TestSuiteResponse>> attachSuiteToPlan(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("planId") UUID planId,
            @Valid @RequestBody AttachSuiteToPlanRequest request) {

        TestPlan testPlan = testPlanService.getTestPlanById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("TestPlan", "id", planId));

        // Auth: require contributor access
        UUID currentUserId = UUID.fromString(jwt.getSubject());
        Project project = testPlan.getProject();
        projectAuth.requireContributorAccess(
                project.getProjectId(), project.getWorkspace().getWorkspaceId(), currentUserId);

        TestSuite testSuite = testSuiteService.getTestSuiteById(request.getTestSuiteId())
                .orElseThrow(() -> new ResourceNotFoundException("TestSuite", "id", request.getTestSuiteId()));

        planSuiteService.attachSuiteToPlan(testPlan, testSuite);
        TestSuiteResponse response = testSuiteAssembler.toModel(testSuite);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Test suite attached to plan successfully"));
    }

    @DeleteMapping("/{planId}/test-suites/{suiteId}")
    public ResponseEntity<ApiResponse<Void>> detachSuiteFromPlan(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("planId") UUID planId,
            @PathVariable("suiteId") UUID suiteId) {

        TestPlan testPlan = testPlanService.getTestPlanById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("TestPlan", "id", planId));

        // Auth: require Lead access for detaching suites
        UUID currentUserId = UUID.fromString(jwt.getSubject());
        Project project = testPlan.getProject();
        projectAuth.requireLeadAccess(
                project.getProjectId(), project.getWorkspace().getWorkspaceId(), currentUserId);

        planSuiteService.detachSuiteFromPlan(planId, suiteId);

        return ResponseEntity.ok(ApiResponse.success("Test suite detached from plan successfully"));
    }

    @GetMapping("/{planId}/test-suites")
    public ResponseEntity<ApiResponse<CollectionModel<TestSuiteResponse>>> getSuitesInPlan(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("planId") UUID planId) {

        TestPlan testPlan = testPlanService.getTestPlanById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("TestPlan", "id", planId));

        // Auth: require read access
        UUID currentUserId = UUID.fromString(jwt.getSubject());
        Project project = testPlan.getProject();
        projectAuth.requireProjectAccess(
                project.getProjectId(), project.getWorkspace().getWorkspaceId(), currentUserId);

        List<PlanSuite> planSuites = planSuiteService.getSuitesInPlan(planId);
        List<TestSuite> suites = planSuites.stream().map(PlanSuite::getTestSuite).toList();
        CollectionModel<TestSuiteResponse> collectionModel = testSuiteAssembler.toCollectionModel(suites);

        return ResponseEntity.ok(ApiResponse.success(collectionModel, "Test suites in plan retrieved successfully"));
    }

    // ---- helpers ----

    private List<UserStory> resolveStories(List<UUID> storyIds) {
        if (storyIds == null || storyIds.isEmpty()) return Collections.emptyList();
        return storyIds.stream()
                .map(storyId -> userStoryService.getUserStoryById(storyId)
                        .orElseThrow(() -> new ResourceNotFoundException("UserStory", "id", storyId)))
                .toList();
    }

    private void enrichWithSuiteIds(TestPlanResponse response, UUID testPlanId) {
        List<PlanSuite> planSuites = planSuiteService.getSuitesInPlan(testPlanId);
        List<UUID> suiteIds = planSuites.stream()
                .map(ps -> ps.getTestSuite().getTestSuiteId())
                .toList();
        response.setSuiteIds(suiteIds);
    }
}
