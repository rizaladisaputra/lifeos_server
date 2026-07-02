package com.lifeos.be.prayer.infrastructure.persistence;

import com.lifeos.be.prayer.domain.model.Prayer;
import com.lifeos.be.prayer.domain.repository.PrayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PrayerRepositoryImpl implements PrayerRepository {

    private final SpringDataPrayerRepository springDataRepo;

    @Override
    public List<Prayer> findByUserIdAndDate(UUID userId, LocalDate date) {
        return springDataRepo.findByUserIdAndDateOrderByTimeAsc(userId, date)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Prayer> findById(UUID id) {
        return springDataRepo.findById(id).map(this::toDomain);
    }

    @Override
    public boolean existsByUserIdAndDate(UUID userId, LocalDate date) {
        return springDataRepo.existsByUserIdAndDate(userId, date);
    }

    @Override
    public List<Prayer> saveAll(List<Prayer> prayers) {
        List<PrayerJpaEntity> entities = prayers.stream()
                .map(this::toJpaEntity)
                .collect(Collectors.toList());
        return springDataRepo.saveAll(entities)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Prayer save(Prayer prayer) {
        PrayerJpaEntity entity = toJpaEntity(prayer);
        PrayerJpaEntity saved = springDataRepo.save(entity);
        return toDomain(saved);
    }

    private Prayer toDomain(PrayerJpaEntity entity) {
        return new Prayer(
                entity.getId(),
                entity.getUserId(),
                entity.getName(),
                entity.getTime(),
                entity.getIsCompleted() != null && entity.getIsCompleted(),
                entity.getDate()
        );
    }

    private PrayerJpaEntity toJpaEntity(Prayer prayer) {
        return PrayerJpaEntity.builder()
                .id(prayer.getId())
                .userId(prayer.getUserId())
                .name(prayer.getName())
                .time(prayer.getTime())
                .isCompleted(prayer.isCompleted())
                .date(prayer.getDate())
                .build();
    }
}
