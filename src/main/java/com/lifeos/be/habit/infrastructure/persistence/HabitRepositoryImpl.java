package com.lifeos.be.habit.infrastructure.persistence;

import com.lifeos.be.habit.domain.model.Habit;
import com.lifeos.be.habit.domain.model.HabitLog;
import com.lifeos.be.habit.domain.repository.HabitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class HabitRepositoryImpl implements HabitRepository {

    private final SpringDataHabitRepository springDataRepo;

    @Override
    public List<Habit> findByUserId(UUID userId) {
        return springDataRepo.findByUserIdOrderByCreatedAtAsc(userId)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Habit> findById(UUID id) {
        return springDataRepo.findById(id).map(this::toDomain);
    }

    @Override
    public Habit save(Habit habit) {
        HabitJpaEntity entity = toJpaEntity(habit);
        HabitJpaEntity saved = springDataRepo.save(entity);
        return toDomain(saved);
    }

    @Override
    public void deleteById(UUID id) {
        springDataRepo.deleteById(id);
    }

    private Habit toDomain(HabitJpaEntity entity) {
        List<HabitLog> domainLogs = entity.getLogs().stream()
                .map(logEntity -> new HabitLog(
                        logEntity.getId(),
                        logEntity.getHabit().getId(),
                        logEntity.getDate()
                ))
                .collect(Collectors.toList());

        return new Habit(
                entity.getId(),
                entity.getUserId(),
                entity.getName(),
                entity.getEmoji(),
                entity.getCreatedAt(),
                domainLogs
        );
    }

    private HabitJpaEntity toJpaEntity(Habit habit) {
        List<HabitLogJpaEntity> jpaLogs = habit.getLogs().stream()
                .map(log -> HabitLogJpaEntity.builder()
                        .id(log.getId())
                        .date(log.getDate())
                        .build())
                .collect(Collectors.toList());

        HabitJpaEntity entity = HabitJpaEntity.builder()
                .id(habit.getId())
                .userId(habit.getUserId())
                .name(habit.getName())
                .emoji(habit.getEmoji())
                .createdAt(habit.getCreatedAt())
                .logs(jpaLogs)
                .build();

        // Establish bi-directional relationship for Hibernate cascade save
        for (HabitLogJpaEntity logEntity : jpaLogs) {
            logEntity.setHabit(entity);
        }

        return entity;
    }
}
