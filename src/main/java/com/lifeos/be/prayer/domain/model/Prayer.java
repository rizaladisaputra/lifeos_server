package com.lifeos.be.prayer.domain.model;

import java.time.LocalDate;
import java.util.UUID;

public class Prayer {

    private final UUID id;
    private final UUID userId;
    private final String name;
    private final String time;
    private boolean completed;
    private final LocalDate date;

    public Prayer(UUID id, UUID userId, String name, String time, boolean completed, LocalDate date) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.time = time;
        this.completed = completed;
        this.date = date;
    }

    public static Prayer create(UUID userId, String name, String time, LocalDate date) {
        return new Prayer(UUID.randomUUID(), userId, name, time, false, date);
    }

    // =========================================
    // Rich Domain Model Logic
    // =========================================

    public void toggleCompletion() {
        this.completed = !this.completed;
    }

    public void assertOwner(UUID requestingUserId) {
        if (!this.userId.equals(requestingUserId)) {
            throw new SecurityException("You do not have permission to modify this prayer entry");
        }
    }

    // =========================================
    // Getters
    // =========================================

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getName() { return name; }
    public String getTime() { return time; }
    public boolean isCompleted() { return completed; }
    public LocalDate getDate() { return date; }
}
