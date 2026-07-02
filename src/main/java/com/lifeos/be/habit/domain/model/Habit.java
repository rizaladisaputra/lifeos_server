package com.lifeos.be.habit.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Habit {

    private final UUID id;
    private final UUID userId;
    private String name;
    private String emoji;
    private final LocalDateTime createdAt;
    private final List<HabitLog> logs;

    public Habit(UUID id, UUID userId, String name, String emoji, LocalDateTime createdAt, List<HabitLog> logs) {
        this.id = id;
        this.userId = userId;
        setName(name);
        this.emoji = emoji;
        this.createdAt = createdAt;
        this.logs = logs != null ? logs : new ArrayList<>();
    }

    public static Habit create(UUID userId, String name, String emoji) {
        return new Habit(UUID.randomUUID(), userId, name, emoji, LocalDateTime.now(), new ArrayList<>());
    }

    // =========================================
    // Rich Domain Model Logic
    // =========================================

    public boolean isCompletedOn(LocalDate date) {
        return logs.stream().anyMatch(log -> log.getDate().equals(date));
    }

    public void assertOwner(UUID requestingUserId) {
        if (!this.userId.equals(requestingUserId)) {
            throw new SecurityException("You do not have permission to modify this habit");
        }
    }

    private void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Habit name cannot be empty");
        }
        this.name = name;
    }

    // =========================================
    // Getters
    // =========================================

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmoji() { return emoji; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<HabitLog> getLogs() { return logs; }
}
