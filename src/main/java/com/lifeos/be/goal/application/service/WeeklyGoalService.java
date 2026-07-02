package com.lifeos.be.goal.application.service;

import com.lifeos.be.gamification.domain.repository.UserRepository;
import com.lifeos.be.goal.application.dto.WeeklyGoalDto;
import com.lifeos.be.goal.domain.model.WeeklyGoal;
import com.lifeos.be.goal.domain.repository.WeeklyGoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WeeklyGoalService {

    private final WeeklyGoalRepository weeklyGoalRepository;
    private final UserRepository userRepository;

    private UUID getCurrentUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email))
                .getId();
    }

    public List<WeeklyGoalDto> getWeeklyGoals() {
        UUID userId = getCurrentUserId();
        List<WeeklyGoal> goals = weeklyGoalRepository.findByUserId(userId);
        return goals.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public WeeklyGoalDto createWeeklyGoal(WeeklyGoalDto dto) {
        UUID userId = getCurrentUserId();
        WeeklyGoal goal = WeeklyGoal.create(
                userId, dto.getTitle(), dto.getEmoji(),
                dto.getTarget(), dto.getUnit()
        );
        return toDto(weeklyGoalRepository.save(goal));
    }

    @Transactional
    public WeeklyGoalDto incrementWeeklyGoal(UUID id, int amount) {
        UUID userId = getCurrentUserId();
        WeeklyGoal goal = weeklyGoalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Weekly goal not found: " + id));

        goal.assertOwner(userId);
        goal.increment(amount);

        return toDto(weeklyGoalRepository.save(goal));
    }

    @Transactional
    public void deleteWeeklyGoal(UUID id) {
        UUID userId = getCurrentUserId();
        WeeklyGoal goal = weeklyGoalRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Weekly goal not found: " + id));

        goal.assertOwner(userId);
        weeklyGoalRepository.deleteById(id);
    }

    private WeeklyGoalDto toDto(WeeklyGoal goal) {
        return WeeklyGoalDto.builder()
                .id(goal.getId())
                .title(goal.getTitle())
                .emoji(goal.getEmoji())
                .current(goal.getCurrent())
                .target(goal.getTarget())
                .unit(goal.getUnit())
                .build();
    }
}
