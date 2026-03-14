package com.group05.TC_LLM_Generator.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for quick status-only update on a user story
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStoryStatusRequest {

    @NotBlank(message = "Status is required")
    private String status;
}
