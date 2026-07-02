package com.lifeos.be.planner.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityDto {
    private UUID id;

    @NotBlank(message = "Time is required")
    private String time;

    private String emoji;

    @NotBlank(message = "Title is required")
    private String title;

    private String category;
    private Boolean isCompleted;

    @NotNull(message = "Date is required")
    private LocalDate date;
}
