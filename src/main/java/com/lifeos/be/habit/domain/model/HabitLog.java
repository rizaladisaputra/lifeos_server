package com.lifeos.be.habit.domain.model;

import java.time.LocalDate;
import java.util.UUID;

public class HabitLog {

    private final UUID id;
    private final UUID habitId;
    private final LocalDate date;

    public HabitLog(UUID id, UUID habitId, LocalDate date) {
        this.id = id;
        this.habitId = habitId;
        this.date = date;
    }

    public static HabitLog create(UUID habitId, LocalDate date) {
        return new HabitLog(UUID.randomUUID(), habitId, date);
    }

    public UUID getId() { return id; }
    public UUID getHabitId() { return habitId; }
    public LocalDate getDate() { return date; }
}
