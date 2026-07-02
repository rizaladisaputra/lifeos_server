package com.lifeos.be.planner.infrastructure.web;

import com.lifeos.be.planner.application.dto.ActivityDto;
import com.lifeos.be.planner.application.service.ActivityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @GetMapping
    public ResponseEntity<List<ActivityDto>> getActivities(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(activityService.getActivitiesByDate(date));
    }

    @PostMapping
    public ResponseEntity<ActivityDto> createActivity(@Valid @RequestBody ActivityDto dto) {
        return ResponseEntity.ok(activityService.createActivity(dto));
    }

    @PutMapping("/{id}/toggle")
    public ResponseEntity<ActivityDto> toggleActivity(@PathVariable UUID id) {
        return ResponseEntity.ok(activityService.toggleActivity(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable UUID id) {
        activityService.deleteActivity(id);
        return ResponseEntity.noContent().build();
    }
}
