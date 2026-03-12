package com.group05.TC_LLM_Generator.application.service;

import com.group05.TC_LLM_Generator.application.port.out.UserStoryRepositoryPort;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserStory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Application Service for UserStory entity
 * Handles CRUD operations and user story-related use cases
 */
import com.group05.TC_LLM_Generator.domain.event.EntityChangedEvent;
import com.group05.TC_LLM_Generator.domain.event.EntityChangedEvent.Action;
import com.group05.TC_LLM_Generator.domain.event.EntityChangedEvent.EntityType;
import org.springframework.context.ApplicationEventPublisher;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserStoryService {

    private final UserStoryRepositoryPort userStoryRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Create a new user story
     */
    @Transactional
    public UserStory createUserStory(UserStory userStory, String performedByUserId) {
        UserStory saved = userStoryRepository.save(userStory);
        
        eventPublisher.publishEvent(new EntityChangedEvent(
                this, EntityType.STORY, Action.CREATED,
                saved.getUserStoryId().toString(),
                saved.getProject().getProjectId().toString(),
                null, 
                performedByUserId
        ));
        
        return saved;
    }

    /**
     * Get user story by ID
     */
    public Optional<UserStory> getUserStoryById(UUID userStoryId) {
        return userStoryRepository.findById(userStoryId);
    }

    /**
     * Get all user stories
     */
    public List<UserStory> getAllUserStories() {
        return userStoryRepository.findAll();
    }

    /**
     * Get all user stories with pagination
     */
    public Page<UserStory> getAllUserStories(Pageable pageable) {
        return userStoryRepository.findAll(pageable);
    }

    /**
     * Get user stories by project ID
     */
    public List<UserStory> getUserStoriesByProject(UUID projectId) {
        return userStoryRepository.findByProjectId(projectId);
    }

    /**
     * Get user stories by project ID with pagination
     */
    public Page<UserStory> getUserStoriesByProject(UUID projectId, Pageable pageable) {
        return userStoryRepository.findByProjectId(projectId, pageable);
    }

    /**
     * Update user story
     */
    @Transactional
    public UserStory updateUserStory(UUID userStoryId, UserStory updatedUserStory, String performedByUserId) {
        UserStory existingUserStory = userStoryRepository.findById(userStoryId)
                .orElseThrow(() -> new IllegalArgumentException("User story not found: " + userStoryId));

        if (updatedUserStory.getTitle() != null) {
            existingUserStory.setTitle(updatedUserStory.getTitle());
        }

        if (updatedUserStory.getDescription() != null) {
            existingUserStory.setDescription(updatedUserStory.getDescription());
        }

        if (updatedUserStory.getStatus() != null) {
            existingUserStory.setStatus(updatedUserStory.getStatus());
        }

        UserStory saved = userStoryRepository.save(existingUserStory);
        
        eventPublisher.publishEvent(new EntityChangedEvent(
                this, EntityType.STORY, Action.UPDATED,
                saved.getUserStoryId().toString(),
                saved.getProject().getProjectId().toString(),
                null, 
                performedByUserId
        ));

        return saved;
    }

    /**
     * Delete user story by ID
     */
    @Transactional
    public void deleteUserStory(UUID userStoryId, String performedByUserId) {
        UserStory existingUserStory = userStoryRepository.findById(userStoryId)
                .orElseThrow(() -> new IllegalArgumentException("User story not found: " + userStoryId));
        
        String projectId = existingUserStory.getProject().getProjectId().toString();

        userStoryRepository.deleteById(userStoryId);
        
        eventPublisher.publishEvent(new EntityChangedEvent(
                this, EntityType.STORY, Action.DELETED,
                userStoryId.toString(),
                projectId,
                null, 
                performedByUserId
        ));
    }

    /**
     * Check if user story exists
     */
    public boolean userStoryExists(UUID userStoryId) {
        return userStoryRepository.existsById(userStoryId);
    }
}
