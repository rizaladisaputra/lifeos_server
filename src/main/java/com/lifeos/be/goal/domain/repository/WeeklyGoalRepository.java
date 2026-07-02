package com.lifeos.be.goal.domain.repository;

import com.lifeos.be.goal.domain.model.WeeklyGoal;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WeeklyGoalRepository {
    List<WeeklyGoal> findByUserId(UUID userId);
    Optional<WeeklyGoal> findById(UUID id);
    WeeklyGoal save(WeeklyGoal goal);
    void deleteById(UUID id);
}
