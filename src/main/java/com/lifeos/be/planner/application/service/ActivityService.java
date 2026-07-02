package com.lifeos.be.planner.application.service;

import com.lifeos.be.gamification.domain.model.User;
import com.lifeos.be.gamification.domain.repository.UserRepository;
import com.lifeos.be.planner.application.dto.ActivityDto;
import com.lifeos.be.planner.domain.model.Activity;
import com.lifeos.be.planner.domain.repository.ActivityRepository;
import com.lifeos.be.planner.domain.service.PlannerDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final PlannerDomainService plannerDomainService;
    private final UserRepository userRepository;

    private UUID getCurrentUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email))
                .getId();
    }

    public List<ActivityDto> getActivitiesByDate(LocalDate date) {
        UUID userId = getCurrentUserId();
        List<Activity> activities = activityRepository.findByUserIdAndDate(userId, date);
        return activities.stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public ActivityDto createActivity(ActivityDto dto) {
        UUID userId = getCurrentUserId();

        Activity newActivity = Activity.create(
                userId, dto.getTime(), dto.getEmoji(),
                dto.getTitle(), dto.getCategory(), dto.getDate()
        );

        List<Activity> existingActivities = activityRepository.findByUserIdAndDate(userId, dto.getDate());
        if (plannerDomainService.hasTimeConflict(newActivity, existingActivities)) {
            throw new IllegalStateException(
                "Time conflict detected. Activities must be at least 15 minutes apart."
            );
        }

        return toDto(activityRepository.save(newActivity));
    }

    @Transactional
    public ActivityDto toggleActivity(UUID id) {
        UUID userId = getCurrentUserId();
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Activity not found: " + id));

        activity.assertOwner(userId);
        activity.toggleCompletion();

        return toDto(activityRepository.save(activity));
    }

    @Transactional
    public void deleteActivity(UUID id) {
        UUID userId = getCurrentUserId();
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Activity not found: " + id));

        activity.assertOwner(userId);
        activityRepository.deleteById(id);
    }

    private ActivityDto toDto(Activity activity) {
        return ActivityDto.builder()
                .id(activity.getId())
                .time(activity.getTime())
                .emoji(activity.getEmoji())
                .title(activity.getTitle())
                .category(activity.getCategory())
                .isCompleted(activity.isCompleted())
                .date(activity.getDate())
                .build();
    }
}
