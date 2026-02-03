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
 * JPA Entity for plan_suites table
 */
@Entity
@Table(name = "plan_suites")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlanSuite {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "plan_suite_id", nullable = false)
    private UUID planSuiteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_plan_id", referencedColumnName = "test_plan_id", nullable = false)
    private TestPlan testPlan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "test_suite_id", referencedColumnName = "test_suite_id", nullable = false)
    private TestSuite testSuite;

}
