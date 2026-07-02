package com.lifeos.be.prayer.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface SpringDataPrayerRepository extends JpaRepository<PrayerJpaEntity, UUID> {
    List<PrayerJpaEntity> findByUserIdAndDateOrderByTimeAsc(UUID userId, LocalDate date);
    boolean existsByUserIdAndDate(UUID userId, LocalDate date);
}
