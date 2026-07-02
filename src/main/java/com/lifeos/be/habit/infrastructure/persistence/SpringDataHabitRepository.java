package com.lifeos.be.habit.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataHabitRepository extends JpaRepository<HabitJpaEntity, UUID> {
    List<HabitJpaEntity> findByUserIdOrderByCreatedAtAsc(UUID userId);
}
