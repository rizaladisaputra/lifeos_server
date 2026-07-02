package com.lifeos.be.planner.domain.repository;

import com.lifeos.be.planner.domain.model.Activity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ActivityRepository {
    List<Activity> findByUserIdAndDate(UUID userId, LocalDate date);
    Optional<Activity> findById(UUID id);
    Activity save(Activity activity);
    void deleteById(UUID id);
}
