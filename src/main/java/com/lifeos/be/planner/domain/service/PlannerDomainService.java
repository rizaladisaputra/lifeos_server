package com.lifeos.be.planner.domain.service;

import com.lifeos.be.planner.domain.model.Activity;

import java.time.LocalTime;
import java.util.List;

public class PlannerDomainService {

    private static final int MINIMUM_GAP_MINUTES = 15;

    public boolean hasTimeConflict(Activity newActivity, List<Activity> existingActivities) {
        LocalTime newTime = newActivity.parsedTime();

        return existingActivities.stream()
                .filter(existing -> !existing.getId().equals(newActivity.getId()))
                .anyMatch(existing -> {
                    LocalTime existingTime = existing.parsedTime();
                    long diffMinutes = Math.abs(
                        newTime.toSecondOfDay() - existingTime.toSecondOfDay()
                    ) / 60L;
                    return diffMinutes < MINIMUM_GAP_MINUTES;
                });
    }

    public long countCompleted(List<Activity> activities) {
        return activities.stream()
                .filter(Activity::isCompleted)
                .count();
    }

    public double calculateCompletionRate(List<Activity> activities) {
        if (activities.isEmpty()) return 0.0;
        return (countCompleted(activities) / (double) activities.size()) * 100.0;
    }
}
