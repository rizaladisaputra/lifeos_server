package com.lifeos.be.habit.infrastructure.web;

import com.lifeos.be.habit.application.dto.HabitDto;
import com.lifeos.be.habit.application.service.HabitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/habits")
@RequiredArgsConstructor
public class HabitController {

    private final HabitService habitService;

    @GetMapping
    public ResponseEntity<List<HabitDto>> getHabits() {
        return ResponseEntity.ok(habitService.getHabits());
    }

    @PostMapping
    public ResponseEntity<HabitDto> createHabit(@Valid @RequestBody HabitDto dto) {
        return ResponseEntity.ok(habitService.createHabit(dto));
    }

    @PutMapping("/{id}/toggle")
    public ResponseEntity<HabitDto> toggleHabitLog(
            @PathVariable UUID id,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(habitService.toggleHabitLog(id, date));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHabit(@PathVariable UUID id) {
        habitService.deleteHabit(id);
        return ResponseEntity.noContent().build();
    }
}
