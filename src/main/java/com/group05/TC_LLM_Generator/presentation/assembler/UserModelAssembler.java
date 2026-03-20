package com.group05.TC_LLM_Generator.presentation.assembler;

import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserEntity;
import com.group05.TC_LLM_Generator.presentation.controller.UserController;
import com.group05.TC_LLM_Generator.presentation.dto.response.UserResponse;
import com.group05.TC_LLM_Generator.presentation.mapper.UserPresentationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * HATEOAS assembler for User resources
 */
@Component
@RequiredArgsConstructor
public class UserModelAssembler implements RepresentationModelAssembler<UserEntity, UserResponse> {

    private final UserPresentationMapper mapper;

    @Override
    public UserResponse toModel(UserEntity entity) {
        UserResponse response = mapper.toResponse(entity);

        // Add HATEOAS links
        response.add(linkTo(methodOn(UserController.class).getUserById(entity.getUserId())).withSelfRel());
        response.add(linkTo(methodOn(UserController.class).updateUser(null, entity.getUserId(), null)).withRel("update"));
        response.add(linkTo(methodOn(UserController.class).deleteUser(entity.getUserId())).withRel("delete"));
        response.add(linkTo(methodOn(UserController.class).getAllUsers(null, null)).withRel("users"));

        return response;
    }

    @Override
    public CollectionModel<UserResponse> toCollectionModel(Iterable<? extends UserEntity> entities) {
        CollectionModel<UserResponse> collectionModel = RepresentationModelAssembler.super.toCollectionModel(entities);

        collectionModel.add(linkTo(methodOn(UserController.class).getAllUsers(null, null)).withSelfRel());

        return collectionModel;
    }
}
