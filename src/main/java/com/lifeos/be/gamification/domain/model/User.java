package com.lifeos.be.gamification.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Pure Domain Entity untuk User.
 * Bebas dari segala annotation JPA/Hibernate.
 */
public class User {

    private final UUID id;
    private final String email;
    private final String passwordHash;
    private String displayName;
    private int xp;
    private int level;
    private String fcmToken;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public User(UUID id, String email, String passwordHash, String displayName,
                int xp, int level, String fcmToken, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.displayName = displayName;
        this.xp = xp;
        this.level = level;
        this.fcmToken = fcmToken;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static User create(String email, String passwordHash, String displayName) {
        return new User(UUID.randomUUID(), email, passwordHash, displayName, 0, 1, null, LocalDateTime.now(), LocalDateTime.now());
    }

    // =========================================
    // Rich Domain Model Logic
    // =========================================

    public void gainXp(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("XP cannot be negative");
        }
        this.xp += amount;
        while (this.xp >= getXpNeededForNextLevel()) {
            this.xp -= getXpNeededForNextLevel();
            this.level++;
        }
    }

    public int getXpNeededForNextLevel() {
        return this.level * 100;
    }

    public void updateFcmToken(String token) {
        this.fcmToken = token;
    }

    // =========================================
    // Getters
    // =========================================

    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getDisplayName() { return displayName; }
    public int getXp() { return xp; }
    public int getLevel() { return level; }
    public String getFcmToken() { return fcmToken; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
