package com.group05.TC_LLM_Generator.application.service;

import com.group05.TC_LLM_Generator.application.port.out.UserStoryRepositoryPort;
import com.group05.TC_LLM_Generator.domain.model.enums.StoryStatus;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.AcceptanceCriteria;
import com.group05.TC_LLM_Generator.infrastructure.persistence.entity.UserStory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserStoryService {

    private final UserStoryRepositoryPort userStoryRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Create a new user story with optional acceptance criteria (single transaction)
     */
    @Transactional
    public UserStory createUserStory(UserStory userStory, List<AcceptanceCriteria> acceptanceCriteriaList, String performedByUserId) {
        // Link AC entities to the story via cascade
        if (acceptanceCriteriaList != null && !acceptanceCriteriaList.isEmpty()) {
            for (AcceptanceCriteria ac : acceptanceCriteriaList) {
                ac.setUserStory(userStory);
                userStory.getAcceptanceCriteria().add(ac);
            }
        }

        UserStory saved = userStoryRepository.save(userStory);

        // Initialize lazy associations within the transaction (open-in-view: false)
        Hibernate.initialize(saved.getProject());
        Hibernate.initialize(saved.getAcceptanceCriteria());

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
     * Create a new user story (without AC list — backward compatibility)
     */
    @Transactional
    public UserStory createUserStory(UserStory userStory, String performedByUserId) {
        return createUserStory(userStory, null, performedByUserId);
    }

    /**
     * Get user story by ID
     */
    public Optional<UserStory> getUserStoryById(UUID userStoryId) {
        Optional<UserStory> opt = userStoryRepository.findById(userStoryId);
        // Initialize lazy associations within the transaction (open-in-view: false)
        opt.ifPresent(story -> {
            Hibernate.initialize(story.getProject());
            Hibernate.initialize(story.getAcceptanceCriteria());
        });
        return opt;
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
        Page<UserStory> page = userStoryRepository.findAll(pageable);
        page.forEach(story -> {
            Hibernate.initialize(story.getProject());
            Hibernate.initialize(story.getAcceptanceCriteria());
        });
        return page;
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
        Page<UserStory> page = userStoryRepository.findByProjectId(projectId, pageable);
        page.forEach(story -> {
            Hibernate.initialize(story.getProject());
            Hibernate.initialize(story.getAcceptanceCriteria());
        });
        return page;
    }

    /**
     * Update user story (with optional AC replacement)
     */
    @Transactional
    public UserStory updateUserStory(UUID userStoryId, UserStory updatedUserStory,
                                      List<AcceptanceCriteria> newAcceptanceCriteria,
                                      String performedByUserId) {
        UserStory existing = userStoryRepository.findById(userStoryId)
                .orElseThrow(() -> new IllegalArgumentException("User story not found: " + userStoryId));

        if (updatedUserStory.getTitle() != null) {
            existing.setTitle(updatedUserStory.getTitle());
        }
        if (updatedUserStory.getDescription() != null) {
            existing.setDescription(updatedUserStory.getDescription());
        }
        if (updatedUserStory.getAsA() != null) {
            existing.setAsA(updatedUserStory.getAsA());
        }
        if (updatedUserStory.getIWantTo() != null) {
            existing.setIWantTo(updatedUserStory.getIWantTo());
        }
        if (updatedUserStory.getSoThat() != null) {
            existing.setSoThat(updatedUserStory.getSoThat());
        }
        if (updatedUserStory.getStatus() != null) {
            existing.setStatus(updatedUserStory.getStatus());
        }

        // Replace AC list if provided (orphanRemoval handles deletes)
        if (newAcceptanceCriteria != null) {
            existing.getAcceptanceCriteria().clear();
            for (AcceptanceCriteria ac : newAcceptanceCriteria) {
                ac.setUserStory(existing);
                existing.getAcceptanceCriteria().add(ac);
            }
        }

        UserStory saved = userStoryRepository.save(existing);

        // Initialize lazy associations within the transaction (open-in-view: false)
        Hibernate.initialize(saved.getProject());
        Hibernate.initialize(saved.getAcceptanceCriteria());

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
     * Update user story (without AC replacement — backward compatibility)
     */
    @Transactional
    public UserStory updateUserStory(UUID userStoryId, UserStory updatedUserStory, String performedByUserId) {
        return updateUserStory(userStoryId, updatedUserStory, null, performedByUserId);
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

    /**
     * Update user story status with transition validation.
     * Allowed transitions:
     *   DRAFT → READY (requires ≥1 AC)
     *   DRAFT → ARCHIVED
     *   READY → DRAFT
     *   READY → IN_PROGRESS (requires ≥1 TestCase linked via AC)
     *   IN_PROGRESS → DONE (all AC must have ≥1 TestCase)
     *   IN_PROGRESS → READY
     *   DONE → IN_PROGRESS
     *   DONE → ARCHIVED
     *   ARCHIVED → DRAFT
     */
    @Transactional
    public UserStory updateStoryStatus(UUID userStoryId, StoryStatus newStatus, String performedByUserId) {
        UserStory existing = userStoryRepository.findById(userStoryId)
                .orElseThrow(() -> new IllegalArgumentException("User story not found: " + userStoryId));

        Hibernate.initialize(existing.getAcceptanceCriteria());
        Hibernate.initialize(existing.getProject());

        StoryStatus currentStatus = existing.getStatus();

        // Same status — no-op
        if (currentStatus == newStatus) {
            return existing;
        }

        // Validate transition
        validateStatusTransition(currentStatus, newStatus, existing);

        existing.setStatus(newStatus);
        UserStory saved = userStoryRepository.save(existing);

        Hibernate.initialize(saved.getAcceptanceCriteria());

        eventPublisher.publishEvent(new EntityChangedEvent(
                this, EntityType.STORY, Action.UPDATED,
                saved.getUserStoryId().toString(),
                saved.getProject().getProjectId().toString(),
                null,
                performedByUserId
        ));

        return saved;
    }

    private void validateStatusTransition(StoryStatus from, StoryStatus to, UserStory story) {
        switch (from) {
            case DRAFT -> {
                if (to != StoryStatus.IN_PROGRESS) {
                    throw new IllegalStateException(
                            "Invalid transition: DRAFT → " + to + ". Allowed: IN_PROGRESS");
                }
            }
            case IN_PROGRESS -> {
                if (to == StoryStatus.DONE) {
                    // Check all AC have at least 1 test case
                    boolean allACCovered = story.getAcceptanceCriteria().stream()
                            .allMatch(ac -> ac.getTestCases() != null && !ac.getTestCases().isEmpty());
                    if (!allACCovered) {
                        throw new IllegalStateException(
                                "Cannot mark as DONE: all acceptance criteria must have at least 1 test case");
                    }
                } else if (to != StoryStatus.DRAFT) {
                    throw new IllegalStateException(
                            "Invalid transition: IN_PROGRESS → " + to + ". Allowed: DRAFT, DONE");
                }
            }
            case DONE -> {
                if (to != StoryStatus.IN_PROGRESS) {
                    throw new IllegalStateException(
                            "Invalid transition: DONE → " + to + ". Allowed: IN_PROGRESS");
                }
            }
        }
    }

    /**
     * Auto-transition story status after a test case is created or deleted.
     * This is a "soft" transition — it only moves the status when safe to do so,
     * and never throws exceptions.
     *
     * After TC created:
     *   DRAFT → IN_PROGRESS           (first test case added)
     *   IN_PROGRESS → DONE            (all ACs now have ≥1 TC)
     *
     * After TC deleted:
     *   DONE → IN_PROGRESS            (no longer all ACs covered)
     *   IN_PROGRESS → DRAFT           (0 test cases remain)
     */
    @Transactional
    public void tryAutoTransitionAfterTestCaseChange(UUID userStoryId, boolean testCaseCreated) {
        try {
            UserStory story = userStoryRepository.findById(userStoryId).orElse(null);
            if (story == null) return;

            Hibernate.initialize(story.getAcceptanceCriteria());
            Hibernate.initialize(story.getProject());

            StoryStatus current = story.getStatus();
            StoryStatus newStatus = current;

            // Compute test case coverage
            List<AcceptanceCriteria> acs = story.getAcceptanceCriteria();
            boolean hasAnyTC = false;
            boolean allACCovered = true;

            if (acs != null && !acs.isEmpty()) {
                for (AcceptanceCriteria ac : acs) {
                    Hibernate.initialize(ac.getTestCases());
                    boolean acHasTC = ac.getTestCases() != null && !ac.getTestCases().isEmpty();
                    if (acHasTC) hasAnyTC = true;
                    if (!acHasTC) allACCovered = false;
                }
            } else {
                allACCovered = false;
            }

            // Determine new status based on coverage
            if (!hasAnyTC) {
                newStatus = StoryStatus.DRAFT;
            } else if (allACCovered) {
                newStatus = StoryStatus.DONE;
            } else {
                newStatus = StoryStatus.IN_PROGRESS;
            }

            if (newStatus != current) {
                story.setStatus(newStatus);
                userStoryRepository.save(story);

                eventPublisher.publishEvent(new EntityChangedEvent(
                        this, EntityType.STORY, Action.UPDATED,
                        story.getUserStoryId().toString(),
                        story.getProject().getProjectId().toString(),
                        null, "system"
                ));
            }
        } catch (Exception e) {
            // Silent — auto-transition should never break the main operation
            log.warn("Auto-transition failed for story {}: {}", userStoryId, e.getMessage());
        }
    }
}
