package com.group05.TC_LLM_Generator.presentation.controller;

import com.group05.TC_LLM_Generator.application.service.BusinessRuleService;
import com.group05.TC_LLM_Generator.application.service.ProjectAccessChecker;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.BusinessRule;
import com.group05.TC_LLM_Generator.presentation.assembler.BusinessRuleModelAssembler;
import com.group05.TC_LLM_Generator.presentation.dto.common.ApiResponse;
import com.group05.TC_LLM_Generator.presentation.dto.request.CreateBusinessRuleRequest;
import com.group05.TC_LLM_Generator.presentation.dto.request.UpdateBusinessRuleRequest;
import com.group05.TC_LLM_Generator.presentation.dto.response.BusinessRuleResponse;
import com.group05.TC_LLM_Generator.presentation.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/business-rules")
@RequiredArgsConstructor
public class BusinessRuleController {

    private final BusinessRuleService businessRuleService;
    private final BusinessRuleModelAssembler assembler;
    private final PagedResourcesAssembler<BusinessRule> pagedResourcesAssembler;
    private final ProjectAccessChecker accessChecker;

    /**
     * List business rules for a project (paginated)
     * GET /api/v1/projects/{projectId}/business-rules
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PagedModel<BusinessRuleResponse>>> getBusinessRules(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID projectId,
            Pageable pageable) {

        UUID userId = UUID.fromString(jwt.getSubject());
        accessChecker.requireProjectMember(projectId, userId);

        Page<BusinessRule> page = businessRuleService.getByProject(projectId, pageable);
        PagedModel<BusinessRuleResponse> pagedModel = pagedResourcesAssembler.toModel(page, assembler);

        return ResponseEntity.ok(ApiResponse.success(pagedModel));
    }

    /**
     * Get business rules by user story
     * GET /api/v1/projects/{projectId}/business-rules?storyId={storyId}
     */
    @GetMapping(params = "storyId")
    public ResponseEntity<ApiResponse<List<BusinessRuleResponse>>> getBusinessRulesByStory(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID projectId,
            @RequestParam UUID storyId) {

        UUID userId = UUID.fromString(jwt.getSubject());
        accessChecker.requireProjectMember(projectId, userId);

        List<BusinessRule> rules = businessRuleService.getByUserStory(storyId);
        List<BusinessRuleResponse> responses = rules.stream()
                .map(assembler::toModel)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * Get a single business rule
     * GET /api/v1/projects/{projectId}/business-rules/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BusinessRuleResponse>> getBusinessRule(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID projectId,
            @PathVariable UUID id) {

        UUID userId = UUID.fromString(jwt.getSubject());
        accessChecker.requireProjectMember(projectId, userId);

        BusinessRule rule = businessRuleService.getById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BusinessRule", "id", id));

        return ResponseEntity.ok(ApiResponse.success(assembler.toModel(rule)));
    }

    /**
     * Create a business rule
     * POST /api/v1/projects/{projectId}/business-rules
     */
    @PostMapping
    public ResponseEntity<ApiResponse<BusinessRuleResponse>> createBusinessRule(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateBusinessRuleRequest request) {

        UUID userId = UUID.fromString(jwt.getSubject());
        accessChecker.requireContributor(projectId, userId);

        BusinessRule created = businessRuleService.createBusinessRule(
                projectId,
                request.getTitle(),
                request.getDescription(),
                request.getPriority(),
                request.getSource(),
                request.getUserStoryId()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(assembler.toModel(created), "Business rule created successfully"));
    }

    /**
     * Update a business rule
     * PUT /api/v1/projects/{projectId}/business-rules/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BusinessRuleResponse>> updateBusinessRule(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID projectId,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateBusinessRuleRequest request) {

        UUID userId = UUID.fromString(jwt.getSubject());
        accessChecker.requireContributor(projectId, userId);

        BusinessRule updated = businessRuleService.updateBusinessRule(
                id,
                request.getTitle(),
                request.getDescription(),
                request.getPriority(),
                request.getSource(),
                request.getUserStoryId()
        );

        return ResponseEntity.ok(ApiResponse.success(assembler.toModel(updated), "Business rule updated successfully"));
    }

    /**
     * Delete a business rule
     * DELETE /api/v1/projects/{projectId}/business-rules/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBusinessRule(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID projectId,
            @PathVariable UUID id) {

        UUID userId = UUID.fromString(jwt.getSubject());
        accessChecker.requireLead(projectId, userId);

        businessRuleService.deleteBusinessRule(id);
        return ResponseEntity.ok(ApiResponse.success("Business rule deleted successfully"));
    }
}
