package com.lifeos.be.habit.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataHabitLogRepository extends JpaRepository<HabitLogJpaEntity, UUID> {
    Optional<SpringDataHabitLogRepository> findByHabitIdAndDate(UUID habitId, LocalDate date);
    // Use custom interface queries in adapter implementation
}
