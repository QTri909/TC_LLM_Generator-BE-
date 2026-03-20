package com.group05.TC_LLM_Generator.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import java.time.Instant;
import java.util.UUID;

/**
 * Response DTO for TestCase entity with HATEOAS support
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestCaseResponse extends RepresentationModel<TestCaseResponse> {

    private UUID testCaseId;
    private UUID userStoryId;
    private String userStoryTitle;
    private UUID acceptanceCriteriaId;
    private UUID testCaseTypeId;
    private String testCaseTypeName;
    private String title;
    private String preconditions;
    private String steps;
    private String expectedResult;
    private String customFieldsJson;
    private String status;
    private Boolean generatedByAi;
    private Instant createdAt;
}
