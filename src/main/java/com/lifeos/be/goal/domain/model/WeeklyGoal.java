package com.lifeos.be.goal.domain.model;

import java.util.UUID;

public class WeeklyGoal {

    private final UUID id;
    private final UUID userId;
    private String title;
    private String emoji;
    private int current;
    private final int target;
    private String unit;

    public WeeklyGoal(UUID id, UUID userId, String title, String emoji, int current, int target, String unit) {
        this.id = id;
        this.userId = userId;
        setTitle(title);
        this.emoji = emoji;
        this.current = current;
        this.target = target;
        this.unit = unit;
    }

    public static WeeklyGoal create(UUID userId, String title, String emoji, int target, String unit) {
        return new WeeklyGoal(UUID.randomUUID(), userId, title, emoji, 0, target, unit);
    }

    // =========================================
    // Rich Domain Model Logic
    // =========================================

    public void increment(int amount) {
        this.current = Math.min(this.current + amount, this.target);
    }

    public boolean isCompleted() {
        return this.current >= this.target;
    }

    public void assertOwner(UUID requestingUserId) {
        if (!this.userId.equals(requestingUserId)) {
            throw new SecurityException("You do not have permission to modify this goal");
        }
    }

    private void setTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Weekly goal title cannot be empty");
        }
        this.title = title;
    }

    // =========================================
    // Getters
    // =========================================

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getTitle() { return title; }
    public String getEmoji() { return emoji; }
    public int getCurrent() { return current; }
    public int getTarget() { return target; }
    public String getUnit() { return unit; }
}
