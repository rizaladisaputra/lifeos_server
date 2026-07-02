package com.lifeos.be.habit.application.service;

import com.lifeos.be.gamification.domain.repository.UserRepository;
import com.lifeos.be.habit.application.dto.HabitDto;
import com.lifeos.be.habit.domain.model.Habit;
import com.lifeos.be.habit.domain.model.HabitLog;
import com.lifeos.be.habit.domain.repository.HabitLogRepository;
import com.lifeos.be.habit.domain.repository.HabitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HabitService {

    private final HabitRepository habitRepository;
    private final HabitLogRepository habitLogRepository;
    private final UserRepository userRepository;

    private UUID getCurrentUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email))
                .getId();
    }

    public List<HabitDto> getHabits() {
        UUID userId = getCurrentUserId();
        List<Habit> habits = habitRepository.findByUserId(userId);
        return habits.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public HabitDto createHabit(HabitDto dto) {
        UUID userId = getCurrentUserId();
        Habit habit = Habit.create(userId, dto.getName(), dto.getEmoji());
        return toDto(habitRepository.save(habit));
    }

    @Transactional
    public HabitDto toggleHabitLog(UUID id, LocalDate date) {
        UUID userId = getCurrentUserId();
        Habit habit = habitRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Habit not found: " + id));
        habit.assertOwner(userId);

        Optional<HabitLog> logOpt = habitLogRepository.findByHabitIdAndDate(id, date);
        if (logOpt.isPresent()) {
            habitLogRepository.delete(logOpt.get());
        } else {
            habitLogRepository.save(HabitLog.create(id, date));
        }

        Habit updated = habitRepository.findById(id).orElseThrow();
        return toDto(updated);
    }

    @Transactional
    public void deleteHabit(UUID id) {
        UUID userId = getCurrentUserId();
        Habit habit = habitRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Habit not found: " + id));
        habit.assertOwner(userId);
        habitRepository.deleteById(id);
    }

    private HabitDto toDto(Habit habit) {
        List<LocalDate> dates = habit.getLogs().stream()
                .map(HabitLog::getDate)
                .collect(Collectors.toList());

        return HabitDto.builder()
                .id(habit.getId())
                .name(habit.getName())
                .emoji(habit.getEmoji())
                .completedDates(dates)
                .completedToday(habit.isCompletedOn(LocalDate.now()))
                .build();
    }
}
