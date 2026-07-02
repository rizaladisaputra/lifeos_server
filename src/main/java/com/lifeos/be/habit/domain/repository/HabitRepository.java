package com.lifeos.be.habit.domain.repository;

import com.lifeos.be.habit.domain.model.Habit;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HabitRepository {
    List<Habit> findByUserId(UUID userId);
    Optional<Habit> findById(UUID id);
    Habit save(Habit habit);
    void deleteById(UUID id);
}
