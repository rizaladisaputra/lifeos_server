package com.lifeos.be.planner.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface SpringDataActivityRepository extends JpaRepository<ActivityJpaEntity, UUID> {
    List<ActivityJpaEntity> findByUserIdAndDateOrderByTimeAsc(UUID userId, LocalDate date);
}
