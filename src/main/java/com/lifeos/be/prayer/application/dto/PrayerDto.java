package com.lifeos.be.prayer.application.dto;

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
public class PrayerDto {
    private UUID id;

    @NotBlank(message = "Name is required")
    private String name;

    private String time;
    private Boolean isCompleted;

    @NotNull(message = "Date is required")
    private LocalDate date;
}
