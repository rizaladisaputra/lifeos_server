package com.lifeos.be.habit.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HabitDto {
    private UUID id;

    @NotBlank(message = "Habit name is required")
    private String name;

    private String emoji;
    private List<LocalDate> completedDates;
    private boolean completedToday;
}
