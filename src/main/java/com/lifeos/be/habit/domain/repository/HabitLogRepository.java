package com.lifeos.be.habit.domain.repository;

import com.lifeos.be.habit.domain.model.HabitLog;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface HabitLogRepository {
    Optional<HabitLog> findByHabitIdAndDate(UUID habitId, LocalDate date);
    HabitLog save(HabitLog log);
    void delete(HabitLog log);
}
