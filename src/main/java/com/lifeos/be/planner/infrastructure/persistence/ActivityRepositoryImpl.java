package com.lifeos.be.planner.infrastructure.persistence;

import com.lifeos.be.planner.domain.model.Activity;
import com.lifeos.be.planner.domain.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ActivityRepositoryImpl implements ActivityRepository {

    private final SpringDataActivityRepository springDataRepo;

    @Override
    public List<Activity> findByUserIdAndDate(UUID userId, LocalDate date) {
        return springDataRepo.findByUserIdAndDateOrderByTimeAsc(userId, date)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Activity> findById(UUID id) {
        return springDataRepo.findById(id).map(this::toDomain);
    }

    @Override
    public Activity save(Activity activity) {
        ActivityJpaEntity entity = toJpaEntity(activity);
        ActivityJpaEntity saved = springDataRepo.save(entity);
        return toDomain(saved);
    }

    @Override
    public void deleteById(UUID id) {
        springDataRepo.deleteById(id);
    }

    private Activity toDomain(ActivityJpaEntity entity) {
        return new Activity(
                entity.getId(),
                entity.getUserId(),
                entity.getTime(),
                entity.getEmoji(),
                entity.getTitle(),
                entity.getCategory(),
                entity.getIsCompleted() != null && entity.getIsCompleted(),
                entity.getDate()
        );
    }

    private ActivityJpaEntity toJpaEntity(Activity activity) {
        return ActivityJpaEntity.builder()
                .id(activity.getId())
                .userId(activity.getUserId())
                .time(activity.getTime())
                .emoji(activity.getEmoji())
                .title(activity.getTitle())
                .category(activity.getCategory())
                .isCompleted(activity.isCompleted())
                .date(activity.getDate())
                .build();
    }
}
