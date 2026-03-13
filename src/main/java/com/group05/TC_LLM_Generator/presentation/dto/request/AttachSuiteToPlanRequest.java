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
public class AttachSuiteToPlanRequest {

    @NotNull(message = "Test suite ID is required")
    private UUID testSuiteId;
}
