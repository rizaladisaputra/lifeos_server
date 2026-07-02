package com.lifeos.be.planner.application.service;

import com.lifeos.be.gamification.domain.model.User;
import com.lifeos.be.gamification.domain.repository.UserRepository;
import com.lifeos.be.planner.application.dto.ActivityDto;
import com.lifeos.be.planner.domain.model.Activity;
import com.lifeos.be.planner.domain.repository.ActivityRepository;
import com.lifeos.be.planner.domain.service.PlannerDomainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ActivityService Tests (Hexagonal Architecture)")
class ActivityServiceTest {

    @Mock
    private ActivityRepository activityRepository;

    @Mock
    private UserRepository userRepository;

    @Spy
    private PlannerDomainService plannerDomainService;

    @InjectMocks
    private ActivityService activityService;

    private UUID testUserId;
    private User testUserEntity;
    private Activity testActivity;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testUserEntity = new User(testUserId, "test@lifeos.com", "hash", "Test User", 0, 1, null, null, null);

        testActivity = new Activity(
                UUID.randomUUID(), testUserId, "09:00", "📚",
                "Baca Buku", "LEARNING", false, LocalDate.now()
        );

        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(auth);
        lenient().when(auth.getName()).thenReturn("test@lifeos.com");
        SecurityContextHolder.setContext(securityContext);

        lenient().when(userRepository.findByEmail("test@lifeos.com"))
                .thenReturn(Optional.of(testUserEntity));
    }

    @Nested
    @DisplayName("getActivitiesByDate()")
    class GetActivitiesTests {

        @Test
        @DisplayName("should return activities list for given date")
        void shouldReturnActivitiesByDate() {
            when(activityRepository.findByUserIdAndDate(testUserId, LocalDate.now()))
                    .thenReturn(List.of(testActivity));

            List<ActivityDto> result = activityService.getActivitiesByDate(LocalDate.now());

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getTitle()).isEqualTo("Baca Buku");
            assertThat(result.get(0).getIsCompleted()).isFalse();
        }

        @Test
        @DisplayName("should return empty list when no activities")
        void shouldReturnEmptyListWhenNoActivities() {
            when(activityRepository.findByUserIdAndDate(testUserId, LocalDate.now()))
                    .thenReturn(List.of());

            List<ActivityDto> result = activityService.getActivitiesByDate(LocalDate.now());

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("toggleActivity()")
    class ToggleActivityTests {

        @Test
        @DisplayName("should toggle isCompleted from false to true via Domain Entity")
        void shouldToggleToTrue() {
            UUID activityId = testActivity.getId();
            when(activityRepository.findById(activityId)).thenReturn(Optional.of(testActivity));
            when(activityRepository.save(any(Activity.class))).thenAnswer(inv -> inv.getArgument(0));

            ActivityDto result = activityService.toggleActivity(activityId);

            assertThat(result.getIsCompleted()).isTrue();
        }

        @Test
        @DisplayName("should throw SecurityException when toggling activity owned by another user")
        void shouldThrowWhenNotOwner() {
            UUID otherUserId = UUID.randomUUID();
            Activity otherActivity = new Activity(
                    UUID.randomUUID(), otherUserId, "10:00", null,
                    "Other Task", null, false, LocalDate.now()
            );

            when(activityRepository.findById(otherActivity.getId()))
                    .thenReturn(Optional.of(otherActivity));

            assertThatThrownBy(() -> activityService.toggleActivity(otherActivity.getId()))
                    .isInstanceOf(SecurityException.class);
        }
    }

    @Nested
    @DisplayName("createActivity()")
    class CreateActivityTests {

        @Test
        @DisplayName("should create activity successfully when no time conflict")
        void shouldCreateActivity() {
            ActivityDto dto = ActivityDto.builder()
                    .time("14:00").title("Olahraga").emoji("🏃")
                    .category("HEALTH").date(LocalDate.now())
                    .build();

            when(activityRepository.findByUserIdAndDate(testUserId, LocalDate.now()))
                    .thenReturn(List.of());
            when(activityRepository.save(any(Activity.class))).thenAnswer(inv -> inv.getArgument(0));

            ActivityDto result = activityService.createActivity(dto);

            assertThat(result.getTitle()).isEqualTo("Olahraga");
            verify(activityRepository, times(1)).save(any(Activity.class));
        }

        @Test
        @DisplayName("should throw exception when activity has time conflict")
        void shouldThrowWhenTimeConflict() {
            ActivityDto dto = ActivityDto.builder()
                    .time("09:05").title("Konflik").date(LocalDate.now()).emoji("❌")
                    .build();

            when(activityRepository.findByUserIdAndDate(testUserId, LocalDate.now()))
                    .thenReturn(List.of(testActivity));

            assertThatThrownBy(() -> activityService.createActivity(dto))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Time conflict");
        }
    }
}
