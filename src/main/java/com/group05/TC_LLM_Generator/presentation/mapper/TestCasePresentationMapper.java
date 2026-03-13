package com.group05.TC_LLM_Generator.presentation.mapper;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.TestCase;
import com.group05.TC_LLM_Generator.presentation.dto.request.CreateTestCaseRequest;
import com.group05.TC_LLM_Generator.presentation.dto.request.UpdateTestCaseRequest;
import com.group05.TC_LLM_Generator.presentation.dto.response.TestCaseResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct mapper for TestCase presentation layer
 */
@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TestCasePresentationMapper {

    /**
     * Map TestCase to TestCaseResponse
     */
    @Mapping(target = "userStoryId", source = "userStory.userStoryId")
    @Mapping(target = "userStoryTitle", source = "userStory.title")
    @Mapping(target = "acceptanceCriteriaId", source = "acceptanceCriteria.acceptanceCriteriaId")
    @Mapping(target = "testCaseTypeId", source = "testCaseType.testCaseTypeId")
    @Mapping(target = "testCaseTypeName", source = "testCaseType.name")
    TestCaseResponse toResponse(TestCase entity);

    /**
     * Map list of TestCase to list of TestCaseResponse
     */
    List<TestCaseResponse> toResponseList(List<TestCase> entities);

    /**
     * Map CreateTestCaseRequest to TestCase
     */
    @Mapping(target = "testCaseId", ignore = true)
    @Mapping(target = "userStory", ignore = true)
    @Mapping(target = "acceptanceCriteria", ignore = true)
    @Mapping(target = "testCaseType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    TestCase toEntity(CreateTestCaseRequest request);

    /**
     * Update TestCase from UpdateTestCaseRequest
     */
    @Mapping(target = "testCaseId", ignore = true)
    @Mapping(target = "userStory", ignore = true)
    @Mapping(target = "acceptanceCriteria", ignore = true)
    @Mapping(target = "testCaseType", ignore = true)
    @Mapping(target = "generatedByAi", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntity(UpdateTestCaseRequest request, @MappingTarget TestCase entity);
}
