package com.group05.TC_LLM_Generator.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

/**
 * JPA Entity for test_cases table
 */
@Entity
@Table(name = "test_cases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestCase {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "test_case_id", nullable = false)
    private UUID testCaseId;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "user_story_id", referencedColumnName = "user_story_id")
    private UserStory userStory;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "acceptance_criteria_id", referencedColumnName = "acceptance_criteria_id")
    private AcceptanceCriteria acceptanceCriteria;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "test_case_type_id", referencedColumnName = "test_case_type_id")
    private TestCaseType testCaseType;

    @Column(name = "title", nullable = false, length = 500)
    private String title;

    @Column(name = "preconditions", columnDefinition = "TEXT")
    private String preconditions;

    @Column(name = "steps", columnDefinition = "TEXT")
    private String steps;

    @Column(name = "expected_result", columnDefinition = "TEXT")
    private String expectedResult;

    @Column(name = "custom_fields_json", nullable = false)
    @JdbcTypeCode(SqlTypes.JSON)
    private String customFieldsJson;

    @Column(name = "generated_by_ai", nullable = false)
    private Boolean generatedByAi;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private Instant createdAt;

}
