package com.group05.TC_LLM_Generator.presentation.controller;

import com.group05.TC_LLM_Generator.application.service.BusinessRuleService;
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

    /**
     * List business rules for a project (paginated)
     * GET /api/v1/projects/{projectId}/business-rules
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PagedModel<BusinessRuleResponse>>> getBusinessRules(
            @PathVariable UUID projectId,
            Pageable pageable) {

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
            @PathVariable UUID projectId,
            @RequestParam UUID storyId) {

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
            @PathVariable UUID projectId,
            @PathVariable UUID id) {

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
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateBusinessRuleRequest request) {

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
            @PathVariable UUID projectId,
            @PathVariable UUID id,
            @Valid @RequestBody UpdateBusinessRuleRequest request) {

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
            @PathVariable UUID projectId,
            @PathVariable UUID id) {

        businessRuleService.deleteBusinessRule(id);
        return ResponseEntity.ok(ApiResponse.success("Business rule deleted successfully"));
    }
}
