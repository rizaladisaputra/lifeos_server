package com.lifeos.be.goal.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyGoalDto {
    private UUID id;

    @NotBlank(message = "Title is required")
    private String title;

    private String emoji;
    private Integer current;

    @NotNull(message = "Target is required")
    private Integer target;

    private String unit;
}
