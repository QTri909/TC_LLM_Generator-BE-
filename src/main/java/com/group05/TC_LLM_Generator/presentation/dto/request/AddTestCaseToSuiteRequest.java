package com.group05.TC_LLM_Generator.presentation.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddTestCaseToSuiteRequest {

    @NotNull(message = "Test case ID is required")
    private UUID testCaseId;
}
