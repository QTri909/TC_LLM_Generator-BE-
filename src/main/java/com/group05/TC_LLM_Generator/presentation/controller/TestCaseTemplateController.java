package com.group05.TC_LLM_Generator.presentation.controller;

import com.group05.TC_LLM_Generator.application.service.ProjectAccessChecker;
import com.group05.TC_LLM_Generator.application.service.TestCaseTemplateService;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TemplateField;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestCaseTemplate;
import com.group05.TC_LLM_Generator.presentation.dto.common.ApiResponse;
import com.group05.TC_LLM_Generator.presentation.dto.request.CreateTestCaseTemplateRequest;
import com.group05.TC_LLM_Generator.presentation.dto.response.TestCaseTemplateResponse;
import com.group05.TC_LLM_Generator.presentation.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/projects/{projectId}/templates")
@RequiredArgsConstructor
public class TestCaseTemplateController {

    private final TestCaseTemplateService templateService;
    private final ProjectAccessChecker accessChecker;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TestCaseTemplateResponse>>> getTemplates(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID projectId) {

        UUID userId = UUID.fromString(jwt.getSubject());
        accessChecker.requireProjectMember(projectId, userId);

        List<TestCaseTemplate> templates = templateService.getByProject(projectId);
        List<TestCaseTemplateResponse> responses = templates.stream()
                .map(t -> toResponse(t, templateService.getFieldsByTemplate(t.getTestCaseTemplateId())))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TestCaseTemplateResponse>> getTemplate(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID projectId,
            @PathVariable UUID id) {

        UUID userId = UUID.fromString(jwt.getSubject());
        accessChecker.requireProjectMember(projectId, userId);

        TestCaseTemplate template = templateService.getById(id)
                .orElseThrow(() -> new ResourceNotFoundException("TestCaseTemplate", "id", id));

        List<TemplateField> fields = templateService.getFieldsByTemplate(id);
        return ResponseEntity.ok(ApiResponse.success(toResponse(template, fields)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TestCaseTemplateResponse>> createTemplate(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateTestCaseTemplateRequest request) {

        UUID userId = UUID.fromString(jwt.getSubject());
        accessChecker.requireContributor(projectId, userId);

        TestCaseTemplate created = templateService.createTemplate(
                projectId, request.getName(), request.getDescription(), request.getIsDefault());

        // If fields provided, save them
        List<TemplateField> savedFields = List.of();
        if (request.getFields() != null && !request.getFields().isEmpty()) {
            List<TemplateField> fields = request.getFields().stream()
                    .map(f -> TemplateField.builder()
                            .fieldKey(f.getFieldKey())
                            .fieldLabel(f.getFieldLabel())
                            .fieldType(f.getFieldType())
                            .isRequired(f.getIsRequired() != null ? f.getIsRequired() : false)
                            .build())
                    .collect(Collectors.toList());
            savedFields = templateService.replaceFields(created.getTestCaseTemplateId(), fields);
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(toResponse(created, savedFields), "Template created successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TestCaseTemplateResponse>> updateTemplate(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID projectId,
            @PathVariable UUID id,
            @RequestBody Map<String, Object> request) {

        UUID userId = UUID.fromString(jwt.getSubject());
        accessChecker.requireContributor(projectId, userId);

        String name = (String) request.get("name");
        String description = (String) request.get("description");
        Boolean isDefault = (Boolean) request.get("isDefault");

        TestCaseTemplate updated = templateService.updateTemplate(id, name, description, isDefault);
        List<TemplateField> fields = templateService.getFieldsByTemplate(id);

        return ResponseEntity.ok(ApiResponse.success(toResponse(updated, fields), "Template updated successfully"));
    }

    @PutMapping("/{id}/fields")
    public ResponseEntity<ApiResponse<List<TestCaseTemplateResponse.TemplateFieldResponse>>> replaceFields(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID projectId,
            @PathVariable UUID id,
            @Valid @RequestBody List<CreateTestCaseTemplateRequest.FieldInput> fieldInputs) {

        UUID userId = UUID.fromString(jwt.getSubject());
        accessChecker.requireContributor(projectId, userId);

        List<TemplateField> fields = fieldInputs.stream()
                .map(f -> TemplateField.builder()
                        .fieldKey(f.getFieldKey())
                        .fieldLabel(f.getFieldLabel())
                        .fieldType(f.getFieldType())
                        .isRequired(f.getIsRequired() != null ? f.getIsRequired() : false)
                        .build())
                .collect(Collectors.toList());

        List<TemplateField> saved = templateService.replaceFields(id, fields);
        List<TestCaseTemplateResponse.TemplateFieldResponse> responses = saved.stream()
                .map(this::toFieldResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(responses, "Fields updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTemplate(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID projectId,
            @PathVariable UUID id) {

        UUID userId = UUID.fromString(jwt.getSubject());
        accessChecker.requireLead(projectId, userId);

        try {
            templateService.deleteTemplate(id);
            return ResponseEntity.ok(ApiResponse.success("Template deleted successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    // ── Mapping helpers ──

    private TestCaseTemplateResponse toResponse(TestCaseTemplate template, List<TemplateField> fields) {
        return TestCaseTemplateResponse.builder()
                .testCaseTemplateId(template.getTestCaseTemplateId())
                .projectId(template.getProject().getProjectId())
                .name(template.getName())
                .description(template.getDescription())
                .isDefault(template.getIsDefault())
                .createdAt(template.getCreatedAt())
                .fields(fields.stream().map(this::toFieldResponse).collect(Collectors.toList()))
                .build();
    }

    private TestCaseTemplateResponse.TemplateFieldResponse toFieldResponse(TemplateField field) {
        return TestCaseTemplateResponse.TemplateFieldResponse.builder()
                .templateFieldId(field.getTemplateFieldId())
                .fieldKey(field.getFieldKey())
                .fieldLabel(field.getFieldLabel())
                .fieldType(field.getFieldType())
                .isRequired(field.getIsRequired())
                .displayOrder(field.getDisplayOrder())
                .build();
    }
}
