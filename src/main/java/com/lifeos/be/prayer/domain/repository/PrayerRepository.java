package com.lifeos.be.prayer.domain.repository;

import com.lifeos.be.prayer.domain.model.Prayer;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PrayerRepository {
    List<Prayer> findByUserIdAndDate(UUID userId, LocalDate date);
    Optional<Prayer> findById(UUID id);
    boolean existsByUserIdAndDate(UUID userId, LocalDate date);
    List<Prayer> saveAll(List<Prayer> prayers);
    Prayer save(Prayer prayer);
}
