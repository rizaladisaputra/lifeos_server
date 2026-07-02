package com.lifeos.be.goal.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataWeeklyGoalRepository extends JpaRepository<WeeklyGoalJpaEntity, UUID> {
    List<WeeklyGoalJpaEntity> findByUserId(UUID userId);
}
