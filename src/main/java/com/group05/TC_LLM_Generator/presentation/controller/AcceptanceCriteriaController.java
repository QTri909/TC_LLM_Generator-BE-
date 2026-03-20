package com.group05.TC_LLM_Generator.presentation.controller;

import com.group05.TC_LLM_Generator.application.service.AcceptanceCriteriaService;
import com.group05.TC_LLM_Generator.application.service.ProjectAccessChecker;
import com.group05.TC_LLM_Generator.application.service.UserStoryService;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.AcceptanceCriteria;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserStory;
import com.group05.TC_LLM_Generator.presentation.dto.common.ApiResponse;
import com.group05.TC_LLM_Generator.presentation.dto.request.CreateAcceptanceCriteriaRequest;
import com.group05.TC_LLM_Generator.presentation.dto.response.AcceptanceCriteriaResponse;
import com.group05.TC_LLM_Generator.presentation.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AcceptanceCriteriaController {

    private final AcceptanceCriteriaService acService;
    private final UserStoryService userStoryService;
    private final ProjectAccessChecker accessChecker;

    /**
     * Get acceptance criteria by user story ID
     * GET /api/v1/user-stories/{storyId}/acceptance-criteria
     */
    @GetMapping("/api/v1/user-stories/{storyId}/acceptance-criteria")
    public ResponseEntity<ApiResponse<List<AcceptanceCriteriaResponse>>> getByUserStory(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID storyId) {

        UUID userId = UUID.fromString(jwt.getSubject());
        UserStory story = userStoryService.getUserStoryById(storyId)
                .orElseThrow(() -> new ResourceNotFoundException("UserStory", "id", storyId));
        accessChecker.requireProjectMember(story.getProject().getProjectId(), userId);

        List<AcceptanceCriteria> criteria = acService.getByUserStoryId(storyId);
        return ResponseEntity.ok(ApiResponse.success(toResponseList(criteria)));
    }

    /**
     * Create a single acceptance criteria
     * POST /api/v1/user-stories/{storyId}/acceptance-criteria
     */
    @PostMapping("/api/v1/user-stories/{storyId}/acceptance-criteria")
    public ResponseEntity<ApiResponse<AcceptanceCriteriaResponse>> create(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID storyId,
            @Valid @RequestBody CreateAcceptanceCriteriaRequest request) {

        UUID userId = UUID.fromString(jwt.getSubject());
        UserStory story = userStoryService.getUserStoryById(storyId)
                .orElseThrow(() -> new ResourceNotFoundException("UserStory", "id", storyId));
        accessChecker.requireContributor(story.getProject().getProjectId(), userId);

        AcceptanceCriteria ac = AcceptanceCriteria.builder()
                .userStory(story)
                .content(request.getContent())
                .orderNo(0)
                .completed(false)
                .build();
        AcceptanceCriteria created = acService.createAcceptanceCriteria(ac);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(toResponse(created), "Acceptance criteria created"));
    }

    /**
     * Batch create acceptance criteria
     * POST /api/v1/user-stories/{storyId}/acceptance-criteria/batch
     */
    @PostMapping("/api/v1/user-stories/{storyId}/acceptance-criteria/batch")
    public ResponseEntity<ApiResponse<List<AcceptanceCriteriaResponse>>> batchCreate(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID storyId,
            @RequestBody List<CreateAcceptanceCriteriaRequest> requests) {

        UUID userId = UUID.fromString(jwt.getSubject());
        UserStory story = userStoryService.getUserStoryById(storyId)
                .orElseThrow(() -> new ResourceNotFoundException("UserStory", "id", storyId));
        accessChecker.requireContributor(story.getProject().getProjectId(), userId);

        AtomicInteger order = new AtomicInteger(0);
        List<AcceptanceCriteria> entities = requests.stream()
                .map(r -> AcceptanceCriteria.builder()
                        .userStory(story)
                        .content(r.getContent())
                        .orderNo(order.getAndIncrement())
                        .completed(false)
                        .build())
                .collect(Collectors.toList());

        List<AcceptanceCriteria> saved = acService.saveAll(entities);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(toResponseList(saved), "Acceptance criteria created"));
    }

    /**
     * Get single acceptance criteria by ID
     * GET /api/v1/acceptance-criteria/{id}
     */
    @GetMapping("/api/v1/acceptance-criteria/{id}")
    public ResponseEntity<ApiResponse<AcceptanceCriteriaResponse>> getById(
            @PathVariable UUID id) {

        AcceptanceCriteria ac = acService.getAcceptanceCriteriaById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AcceptanceCriteria", "id", id));

        return ResponseEntity.ok(ApiResponse.success(toResponse(ac)));
    }

    /**
     * Update acceptance criteria
     * PUT /api/v1/acceptance-criteria/{id}
     */
    @PutMapping("/api/v1/acceptance-criteria/{id}")
    public ResponseEntity<ApiResponse<AcceptanceCriteriaResponse>> update(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id,
            @Valid @RequestBody CreateAcceptanceCriteriaRequest request) {

        UUID userId = UUID.fromString(jwt.getSubject());
        AcceptanceCriteria existing = acService.getAcceptanceCriteriaWithUserStory(id)
                .orElseThrow(() -> new ResourceNotFoundException("AcceptanceCriteria", "id", id));
        accessChecker.requireContributor(
                existing.getUserStory().getProject().getProjectId(), userId);

        AcceptanceCriteria updateData = AcceptanceCriteria.builder()
                .content(request.getContent())
                .build();
        AcceptanceCriteria updated = acService.updateAcceptanceCriteria(id, updateData);

        return ResponseEntity.ok(ApiResponse.success(toResponse(updated), "Acceptance criteria updated"));
    }

    /**
     * Delete acceptance criteria
     * DELETE /api/v1/acceptance-criteria/{id}
     */
    @DeleteMapping("/api/v1/acceptance-criteria/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id) {

        UUID userId = UUID.fromString(jwt.getSubject());
        AcceptanceCriteria existing = acService.getAcceptanceCriteriaWithUserStory(id)
                .orElseThrow(() -> new ResourceNotFoundException("AcceptanceCriteria", "id", id));
        accessChecker.requireContributor(
                existing.getUserStory().getProject().getProjectId(), userId);

        acService.deleteAcceptanceCriteria(id);

        return ResponseEntity.ok(ApiResponse.success("Acceptance criteria deleted"));
    }

    // ── Mapping helpers ──

    private AcceptanceCriteriaResponse toResponse(AcceptanceCriteria ac) {
        return AcceptanceCriteriaResponse.builder()
                .acceptanceCriteriaId(ac.getAcceptanceCriteriaId())
                .userStoryId(ac.getUserStory().getUserStoryId())
                .content(ac.getContent())
                .createdAt(ac.getCreatedAt())
                .build();
    }

    private List<AcceptanceCriteriaResponse> toResponseList(List<AcceptanceCriteria> list) {
        return list.stream().map(this::toResponse).collect(Collectors.toList());
    }
}
