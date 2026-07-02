package com.lifeos.be.goal.infrastructure.web;

import com.lifeos.be.goal.application.dto.WeeklyGoalDto;
import com.lifeos.be.goal.application.service.WeeklyGoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/weekly-goals")
@RequiredArgsConstructor
public class WeeklyGoalController {

    private final WeeklyGoalService weeklyGoalService;

    @GetMapping
    public ResponseEntity<List<WeeklyGoalDto>> getWeeklyGoals() {
        return ResponseEntity.ok(weeklyGoalService.getWeeklyGoals());
    }

    @PostMapping
    public ResponseEntity<WeeklyGoalDto> createWeeklyGoal(@Valid @RequestBody WeeklyGoalDto dto) {
        return ResponseEntity.ok(weeklyGoalService.createWeeklyGoal(dto));
    }

    @PutMapping("/{id}/increment")
    public ResponseEntity<WeeklyGoalDto> incrementWeeklyGoal(
            @PathVariable UUID id,
            @RequestParam(value = "amount", defaultValue = "1") int amount) {
        return ResponseEntity.ok(weeklyGoalService.incrementWeeklyGoal(id, amount));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWeeklyGoal(@PathVariable UUID id) {
        weeklyGoalService.deleteWeeklyGoal(id);
        return ResponseEntity.noContent().build();
    }
}
