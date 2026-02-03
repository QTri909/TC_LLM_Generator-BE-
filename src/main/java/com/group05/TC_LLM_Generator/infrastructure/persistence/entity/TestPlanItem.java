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
 * JPA Entity for test_plan_items table
 */
@Entity
@Table(name = "test_plan_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestPlanItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "test_plan_item_id", nullable = false)
    private UUID testPlanItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_plan_id", referencedColumnName = "test_plan_id", nullable = false)
    private TestPlan testPlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_case_id", referencedColumnName = "test_case_id", nullable = false)
    private TestCase testCase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "executed_by_user_id", referencedColumnName = "user_id", nullable = false)
    private UserEntity executedByUser;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "ended_at")
    private Instant endedAt;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

}
