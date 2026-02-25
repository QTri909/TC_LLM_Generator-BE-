package com.group05.TC_LLM_Generator.infrastructure.persistence.mapper;

import com.group05.TC_LLM_Generator.domain.model.entity.UserStory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserStoryMapper {
    @Mapping(target = "id", source = "userStoryId")
    UserStory toDomain(com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserStory entity);

    @Mapping(target = "userStoryId", source = "id")
    @Mapping(target = "project", ignore = true)
    com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserStory toEntity(UserStory domain);
}
