package com.group05.TC_LLM_Generator.presentation.controller;

import com.group05.TC_LLM_Generator.application.service.ProjectAuthorizationService;
import com.group05.TC_LLM_Generator.application.service.ProjectService;
import com.group05.TC_LLM_Generator.application.service.TestSuiteItemService;
import com.group05.TC_LLM_Generator.application.service.TestSuiteService;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.Project;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestCase;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestSuite;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestSuiteItem;
import com.group05.TC_LLM_Generator.presentation.assembler.TestCaseModelAssembler;
import com.group05.TC_LLM_Generator.presentation.assembler.TestSuiteModelAssembler;
import com.group05.TC_LLM_Generator.presentation.dto.common.ApiResponse;
import com.group05.TC_LLM_Generator.presentation.dto.request.AddTestCaseToSuiteRequest;
import com.group05.TC_LLM_Generator.presentation.dto.request.CreateTestSuiteRequest;
import com.group05.TC_LLM_Generator.presentation.dto.request.UpdateTestSuiteRequest;
import com.group05.TC_LLM_Generator.presentation.dto.response.TestCaseResponse;
import com.group05.TC_LLM_Generator.presentation.dto.response.TestSuiteResponse;
import com.group05.TC_LLM_Generator.presentation.exception.ResourceNotFoundException;
import com.group05.TC_LLM_Generator.presentation.mapper.TestSuitePresentationMapper;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/test-suites")
@RequiredArgsConstructor
public class TestSuiteController {

    private final TestSuiteService testSuiteService;
    private final TestSuiteItemService testSuiteItemService;
    private final ProjectService projectService;
    private final ProjectAuthorizationService projectAuth;
    private final TestSuitePresentationMapper mapper;
    private final TestSuiteModelAssembler assembler;
    private final TestCaseModelAssembler testCaseAssembler;
    private final PagedResourcesAssembler<TestSuite> pagedResourcesAssembler;

    @PostMapping
    public ResponseEntity<ApiResponse<TestSuiteResponse>> createTestSuite(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateTestSuiteRequest request) {

        UUID currentUserId = UUID.fromString(jwt.getSubject());

        Project project = projectService.getProjectById(request.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", request.getProjectId()));

        // Auth: require contributor access
        projectAuth.requireContributorAccess(
                project.getProjectId(), project.getWorkspace().getWorkspaceId(), currentUserId);

        TestSuite testSuite = mapper.toEntity(request);
        testSuite.setProject(project);

        TestSuite savedTestSuite = testSuiteService.createTestSuite(testSuite);
        TestSuiteResponse response = assembler.toModel(savedTestSuite);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Test suite created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TestSuiteResponse>> getTestSuiteById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") UUID id) {

        TestSuite testSuite = testSuiteService.getTestSuiteById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestSuite", "id", id));

        // Auth: require read access
        UUID currentUserId = UUID.fromString(jwt.getSubject());
        Project project = testSuite.getProject();
        projectAuth.requireProjectAccess(
                project.getProjectId(), project.getWorkspace().getWorkspaceId(), currentUserId);

        TestSuiteResponse response = assembler.toModel(testSuite);

        return ResponseEntity.ok(ApiResponse.success(response, "Test suite retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedModel<TestSuiteResponse>>> getAllTestSuites(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<TestSuite> page = testSuiteService.getAllTestSuites(pageable);
        PagedModel<TestSuiteResponse> pagedModel = pagedResourcesAssembler.toModel(page, assembler);

        return ResponseEntity.ok(ApiResponse.success(pagedModel, "Test suites retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TestSuiteResponse>> updateTestSuite(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateTestSuiteRequest request) {

        UUID currentUserId = UUID.fromString(jwt.getSubject());

        TestSuite existingTestSuite = testSuiteService.getTestSuiteById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestSuite", "id", id));

        // Auth: require contributor access
        Project project = existingTestSuite.getProject();
        projectAuth.requireContributorAccess(
                project.getProjectId(), project.getWorkspace().getWorkspaceId(), currentUserId);

        mapper.updateEntity(request, existingTestSuite);
        TestSuite updatedTestSuite = testSuiteService.updateTestSuite(id, existingTestSuite);
        TestSuiteResponse response = assembler.toModel(updatedTestSuite);

        return ResponseEntity.ok(ApiResponse.success(response, "Test suite updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTestSuite(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") UUID id) {

        UUID currentUserId = UUID.fromString(jwt.getSubject());

        TestSuite testSuite = testSuiteService.getTestSuiteById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestSuite", "id", id));

        // Auth: require Lead access for deletion
        Project project = testSuite.getProject();
        projectAuth.requireLeadAccess(
                project.getProjectId(), project.getWorkspace().getWorkspaceId(), currentUserId);

        testSuiteService.deleteTestSuite(id);

        return ResponseEntity.ok(ApiResponse.success("Test suite deleted successfully"));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<ApiResponse<PagedModel<TestSuiteResponse>>> getTestSuitesByProject(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("projectId") UUID projectId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Project project = projectService.getProjectById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project", "id", projectId));

        // Auth: require read access
        UUID currentUserId = UUID.fromString(jwt.getSubject());
        projectAuth.requireProjectAccess(
                projectId, project.getWorkspace().getWorkspaceId(), currentUserId);

        Page<TestSuite> page = testSuiteService.getTestSuitesByProject(projectId, pageable);
        PagedModel<TestSuiteResponse> pagedModel = pagedResourcesAssembler.toModel(page, assembler);

        return ResponseEntity.ok(ApiResponse.success(pagedModel, "Test suites retrieved successfully"));
    }

    // ---- TestSuiteItem endpoints: manage test cases in a suite ----

    @PostMapping("/{suiteId}/test-cases")
    public ResponseEntity<ApiResponse<TestCaseResponse>> addTestCaseToSuite(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("suiteId") UUID suiteId,
            @Valid @RequestBody AddTestCaseToSuiteRequest request) {

        TestSuite testSuite = testSuiteService.getTestSuiteById(suiteId)
                .orElseThrow(() -> new ResourceNotFoundException("TestSuite", "id", suiteId));

        // Auth: require contributor access
        UUID currentUserId = UUID.fromString(jwt.getSubject());
        Project project = testSuite.getProject();
        projectAuth.requireContributorAccess(
                project.getProjectId(), project.getWorkspace().getWorkspaceId(), currentUserId);

        TestSuiteItem item = testSuiteItemService.addTestCaseToSuite(suiteId, request.getTestCaseId());
        TestCaseResponse response = testCaseAssembler.toModel(item.getTestCase());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Test case added to suite successfully"));
    }

    @DeleteMapping("/{suiteId}/test-cases/{testCaseId}")
    public ResponseEntity<ApiResponse<Void>> removeTestCaseFromSuite(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("suiteId") UUID suiteId,
            @PathVariable("testCaseId") UUID testCaseId) {

        TestSuite testSuite = testSuiteService.getTestSuiteById(suiteId)
                .orElseThrow(() -> new ResourceNotFoundException("TestSuite", "id", suiteId));

        // Auth: require Lead access for removing test cases from suites
        UUID currentUserId = UUID.fromString(jwt.getSubject());
        Project project = testSuite.getProject();
        projectAuth.requireLeadAccess(
                project.getProjectId(), project.getWorkspace().getWorkspaceId(), currentUserId);

        testSuiteItemService.removeTestCaseFromSuite(suiteId, testCaseId);

        return ResponseEntity.ok(ApiResponse.success("Test case removed from suite successfully"));
    }

    @GetMapping("/{suiteId}/test-cases")
    public ResponseEntity<ApiResponse<CollectionModel<TestCaseResponse>>> getTestCasesInSuite(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable("suiteId") UUID suiteId) {

        TestSuite testSuite = testSuiteService.getTestSuiteById(suiteId)
                .orElseThrow(() -> new ResourceNotFoundException("TestSuite", "id", suiteId));

        // Auth: require read access
        UUID currentUserId = UUID.fromString(jwt.getSubject());
        Project project = testSuite.getProject();
        projectAuth.requireProjectAccess(
                project.getProjectId(), project.getWorkspace().getWorkspaceId(), currentUserId);

        List<TestSuiteItem> items = testSuiteItemService.getTestCasesInSuite(suiteId);
        List<TestCase> testCases = items.stream().map(TestSuiteItem::getTestCase).toList();
        CollectionModel<TestCaseResponse> collectionModel = testCaseAssembler.toCollectionModel(testCases);

        return ResponseEntity.ok(ApiResponse.success(collectionModel, "Test cases in suite retrieved successfully"));
    }
}
