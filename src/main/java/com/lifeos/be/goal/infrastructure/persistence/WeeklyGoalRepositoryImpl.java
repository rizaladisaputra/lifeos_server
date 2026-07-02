package com.lifeos.be.goal.infrastructure.persistence;

import com.lifeos.be.goal.domain.model.WeeklyGoal;
import com.lifeos.be.goal.domain.repository.WeeklyGoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class WeeklyGoalRepositoryImpl implements WeeklyGoalRepository {

    private final SpringDataWeeklyGoalRepository springDataRepo;

    @Override
    public List<WeeklyGoal> findByUserId(UUID userId) {
        return springDataRepo.findByUserId(userId)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<WeeklyGoal> findById(UUID id) {
        return springDataRepo.findById(id).map(this::toDomain);
    }

    @Override
    public WeeklyGoal save(WeeklyGoal goal) {
        WeeklyGoalJpaEntity entity = toJpaEntity(goal);
        WeeklyGoalJpaEntity saved = springDataRepo.save(entity);
        return toDomain(saved);
    }

    @Override
    public void deleteById(UUID id) {
        springDataRepo.deleteById(id);
    }

    private WeeklyGoal toDomain(WeeklyGoalJpaEntity entity) {
        return new WeeklyGoal(
                entity.getId(),
                entity.getUserId(),
                entity.getTitle(),
                entity.getEmoji(),
                entity.getCurrent(),
                entity.getTarget(),
                entity.getUnit()
        );
    }

    private WeeklyGoalJpaEntity toJpaEntity(WeeklyGoal goal) {
        return WeeklyGoalJpaEntity.builder()
                .id(goal.getId())
                .userId(goal.getUserId())
                .title(goal.getTitle())
                .emoji(goal.getEmoji())
                .current(goal.getCurrent())
                .target(goal.getTarget())
                .unit(goal.getUnit())
                .build();
    }
}
