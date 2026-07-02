package com.lifeos.be.prayer.infrastructure.web;

import com.lifeos.be.prayer.application.dto.PrayerDto;
import com.lifeos.be.prayer.application.service.PrayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/prayers")
@RequiredArgsConstructor
public class PrayerController {

    private final PrayerService prayerService;

    @GetMapping
    public ResponseEntity<List<PrayerDto>> getPrayers(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(prayerService.getPrayersByDate(date));
    }

    @PutMapping("/{id}/toggle")
    public ResponseEntity<PrayerDto> togglePrayer(@PathVariable UUID id) {
        return ResponseEntity.ok(prayerService.togglePrayer(id));
    }
}
