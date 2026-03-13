package com.group05.TC_LLM_Generator.presentation.controller;

import com.group05.TC_LLM_Generator.application.service.AcceptanceCriteriaService;
import com.group05.TC_LLM_Generator.application.service.TestCaseService;
import com.group05.TC_LLM_Generator.application.service.UserStoryService;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.AcceptanceCriteria;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestCase;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserStory;
import com.group05.TC_LLM_Generator.presentation.assembler.TestCaseModelAssembler;
import com.group05.TC_LLM_Generator.presentation.dto.common.ApiResponse;
import com.group05.TC_LLM_Generator.presentation.dto.request.CreateTestCaseRequest;
import com.group05.TC_LLM_Generator.presentation.dto.request.UpdateTestCaseRequest;
import com.group05.TC_LLM_Generator.presentation.dto.response.TestCaseResponse;
import com.group05.TC_LLM_Generator.presentation.exception.ResourceNotFoundException;
import com.group05.TC_LLM_Generator.presentation.mapper.TestCasePresentationMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/test-cases")
@RequiredArgsConstructor
public class TestCaseController {

    private final TestCaseService testCaseService;
    private final UserStoryService userStoryService;
    private final AcceptanceCriteriaService acceptanceCriteriaService;
    private final TestCasePresentationMapper mapper;
    private final TestCaseModelAssembler assembler;
    private final PagedResourcesAssembler<TestCase> pagedResourcesAssembler;

    @PostMapping
    public ResponseEntity<ApiResponse<TestCaseResponse>> createTestCase(
            @Valid @RequestBody CreateTestCaseRequest request) {

        TestCase testCase = mapper.toEntity(request);

        // Resolve UserStory reference
        if (request.getUserStoryId() != null) {
            UserStory userStory = userStoryService.getUserStoryById(request.getUserStoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("UserStory", "id", request.getUserStoryId()));
            testCase.setUserStory(userStory);
        }

        // Resolve AcceptanceCriteria reference
        if (request.getAcceptanceCriteriaId() != null) {
            AcceptanceCriteria ac = acceptanceCriteriaService.getAcceptanceCriteriaById(request.getAcceptanceCriteriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("AcceptanceCriteria", "id", request.getAcceptanceCriteriaId()));
            testCase.setAcceptanceCriteria(ac);
        }

        TestCase savedTestCase = testCaseService.createTestCase(testCase);
        TestCaseResponse response = assembler.toModel(savedTestCase);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Test case created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TestCaseResponse>> getTestCaseById(@PathVariable("id") UUID id) {
        TestCase testCase = testCaseService.getTestCaseById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestCase", "id", id));

        TestCaseResponse response = assembler.toModel(testCase);

        return ResponseEntity.ok(ApiResponse.success(response, "Test case retrieved successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedModel<TestCaseResponse>>> getAllTestCases(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<TestCase> page = testCaseService.getAllTestCases(pageable);
        PagedModel<TestCaseResponse> pagedModel = pagedResourcesAssembler.toModel(page, assembler);

        return ResponseEntity.ok(ApiResponse.success(pagedModel, "Test cases retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TestCaseResponse>> updateTestCase(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateTestCaseRequest request) {

        TestCase existingTestCase = testCaseService.getTestCaseById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestCase", "id", id));

        mapper.updateEntity(request, existingTestCase);
        TestCase updatedTestCase = testCaseService.updateTestCase(id, existingTestCase);
        TestCaseResponse response = assembler.toModel(updatedTestCase);

        return ResponseEntity.ok(ApiResponse.success(response, "Test case updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTestCase(@PathVariable("id") UUID id) {
        if (!testCaseService.testCaseExists(id)) {
            throw new ResourceNotFoundException("TestCase", "id", id);
        }

        testCaseService.deleteTestCase(id);

        return ResponseEntity.ok(ApiResponse.success("Test case deleted successfully"));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PagedModel<TestCaseResponse>>> searchTestCases(
            @RequestParam("title") String title,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<TestCase> page = testCaseService.searchTestCasesByTitle(title, pageable);
        PagedModel<TestCaseResponse> pagedModel = pagedResourcesAssembler.toModel(page, assembler);

        return ResponseEntity.ok(ApiResponse.success(pagedModel, "Test cases retrieved successfully"));
    }

    @GetMapping("/acceptance-criteria/{acceptanceCriteriaId}")
    public ResponseEntity<ApiResponse<PagedModel<TestCaseResponse>>> getTestCasesByAcceptanceCriteria(
            @PathVariable("acceptanceCriteriaId") UUID acceptanceCriteriaId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<TestCase> page = testCaseService.getTestCasesByAcceptanceCriteria(acceptanceCriteriaId, pageable);
        PagedModel<TestCaseResponse> pagedModel = pagedResourcesAssembler.toModel(page, assembler);

        return ResponseEntity.ok(ApiResponse.success(pagedModel, "Test cases retrieved successfully"));
    }

    @GetMapping("/user-story/{userStoryId}")
    public ResponseEntity<ApiResponse<PagedModel<TestCaseResponse>>> getTestCasesByUserStory(
            @PathVariable("userStoryId") UUID userStoryId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<TestCase> page = testCaseService.getTestCasesByUserStory(userStoryId, pageable);
        PagedModel<TestCaseResponse> pagedModel = pagedResourcesAssembler.toModel(page, assembler);

        return ResponseEntity.ok(ApiResponse.success(pagedModel, "Test cases retrieved successfully"));
    }
}
