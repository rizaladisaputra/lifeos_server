package com.lifeos.be.prayer.application.service;

import com.lifeos.be.gamification.domain.repository.UserRepository;
import com.lifeos.be.prayer.application.dto.PrayerDto;
import com.lifeos.be.prayer.domain.model.Prayer;
import com.lifeos.be.prayer.domain.repository.PrayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PrayerService {

    private final PrayerRepository prayerRepository;
    private final UserRepository userRepository;

    private static final String[][] DEFAULT_PRAYERS = {
        {"Subuh", "04:45"}, {"Dzuhur", "12:00"}, {"Ashar", "15:15"},
        {"Maghrib", "18:00"}, {"Isya", "19:15"}
    };

    private UUID getCurrentUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email))
                .getId();
    }

    @Transactional
    public List<PrayerDto> getPrayersByDate(LocalDate date) {
        UUID userId = getCurrentUserId();
        
        if (!prayerRepository.existsByUserIdAndDate(userId, date)) {
            seedDailyPrayers(userId, date);
        }

        return prayerRepository.findByUserIdAndDate(userId, date)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public PrayerDto togglePrayer(UUID id) {
        UUID userId = getCurrentUserId();
        Prayer prayer = prayerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Prayer entry not found: " + id));

        prayer.assertOwner(userId);
        prayer.toggleCompletion();

        return toDto(prayerRepository.save(prayer));
    }

    private void seedDailyPrayers(UUID userId, LocalDate date) {
        List<Prayer> prayers = new ArrayList<>();
        for (String[] p : DEFAULT_PRAYERS) {
            prayers.add(Prayer.create(userId, p[0], p[1], date));
        }
        prayerRepository.saveAll(prayers);
    }

    private PrayerDto toDto(Prayer prayer) {
        return PrayerDto.builder()
                .id(prayer.getId())
                .name(prayer.getName())
                .time(prayer.getTime())
                .isCompleted(prayer.isCompleted())
                .date(prayer.getDate())
                .build();
    }
}
