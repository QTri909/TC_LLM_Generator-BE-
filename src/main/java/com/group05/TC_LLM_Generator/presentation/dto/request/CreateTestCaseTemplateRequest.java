package com.group05.TC_LLM_Generator.presentation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTestCaseTemplateRequest {

    @NotBlank(message = "Template name is required")
    @Size(max = 255, message = "Name must be at most 255 characters")
    private String name;

    private String description;

    private Boolean isDefault;

    private List<FieldInput> fields;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldInput {
        @NotBlank
        private String fieldKey;
        @NotBlank
        private String fieldLabel;
        @NotBlank
        private String fieldType;
        private Boolean isRequired;
    }
}
