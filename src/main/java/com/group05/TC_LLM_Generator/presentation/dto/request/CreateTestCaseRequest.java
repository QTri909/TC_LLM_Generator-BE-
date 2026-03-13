package com.group05.TC_LLM_Generator.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Request DTO for creating a new test case
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTestCaseRequest {

    private UUID userStoryId;

    private UUID acceptanceCriteriaId;

    private UUID testCaseTypeId;

    @NotBlank(message = "Title is required")
    @Size(max = 500, message = "Title must not exceed 500 characters")
    private String title;

    private String preconditions;

    private String steps;

    private String expectedResult;

    @Builder.Default
    private String customFieldsJson = "{}";

    @Builder.Default
    private Boolean generatedByAi = false;
}
