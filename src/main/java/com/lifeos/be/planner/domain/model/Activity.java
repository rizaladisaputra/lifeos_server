package com.lifeos.be.planner.domain.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Activity {

    private final UUID id;
    private final UUID userId;
    private String time;
    private String emoji;
    private String title;
    private String category;
    private boolean completed;
    private final LocalDate date;

    public Activity(UUID id, UUID userId, String time, String emoji,
                    String title, String category, boolean completed, LocalDate date) {
        this.id = id;
        this.userId = userId;
        setTime(time);
        this.emoji = emoji;
        setTitle(title);
        this.category = category;
        this.completed = completed;
        this.date = date;
    }

    public static Activity create(UUID userId, String time, String emoji,
                                   String title, String category, LocalDate date) {
        return new Activity(UUID.randomUUID(), userId, time, emoji, title, category, false, date);
    }

    public void toggleCompletion() {
        this.completed = !this.completed;
    }

    public void assertOwner(UUID requestingUserId) {
        if (!this.userId.equals(requestingUserId)) {
            throw new SecurityException("You do not have permission to modify this activity");
        }
    }

    public LocalTime parsedTime() {
        try {
            return LocalTime.parse(this.time, DateTimeFormatter.ofPattern("HH:mm"));
        } catch (Exception e) {
            return LocalTime.MIDNIGHT;
        }
    }

    private void setTime(String time) {
        if (time == null || time.isBlank()) {
            throw new IllegalArgumentException("Activity time cannot be empty");
        }
        this.time = time;
    }

    private void setTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Activity title cannot be empty");
        }
        this.title = title;
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getTime() { return time; }
    public String getEmoji() { return emoji; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public boolean isCompleted() { return completed; }
    public LocalDate getDate() { return date; }
}
