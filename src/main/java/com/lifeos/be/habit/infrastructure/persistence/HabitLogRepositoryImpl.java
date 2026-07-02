package com.lifeos.be.habit.infrastructure.persistence;

import com.lifeos.be.habit.domain.model.HabitLog;
import com.lifeos.be.habit.domain.repository.HabitLogRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class HabitLogRepositoryImpl implements HabitLogRepository {

    private final EntityManager entityManager;

    @Override
    public Optional<HabitLog> findByHabitIdAndDate(UUID habitId, LocalDate date) {
        String query = "SELECT h FROM HabitLogJpaEntity h WHERE h.habit.id = :habitId AND h.date = :date";
        return entityManager.createQuery(query, HabitLogJpaEntity.class)
                .setParameter("habitId", habitId)
                .setParameter("date", date)
                .getResultList()
                .stream()
                .findFirst()
                .map(entity -> new HabitLog(entity.getId(), entity.getHabit().getId(), entity.getDate()));
    }

    @Override
    public HabitLog save(HabitLog log) {
        HabitJpaEntity habitRef = entityManager.getReference(HabitJpaEntity.class, log.getHabitId());
        
        HabitLogJpaEntity entity = HabitLogJpaEntity.builder()
                .id(log.getId())
                .habit(habitRef)
                .date(log.getDate())
                .build();

        entityManager.persist(entity);
        return log;
    }

    @Override
    public void delete(HabitLog log) {
        // Query to find the entity instance first
        String query = "SELECT h FROM HabitLogJpaEntity h WHERE h.habit.id = :habitId AND h.date = :date";
        entityManager.createQuery(query, HabitLogJpaEntity.class)
                .setParameter("habitId", log.getHabitId())
                .setParameter("date", log.getDate())
                .getResultList()
                .forEach(entityManager::remove);
    }
}
