package com.lifeos.be.gamification.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("User Domain Model Tests")
class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = User.create("test@lifeos.com", "hash", "Test User");
    }

    @Nested
    @DisplayName("gainXp()")
    class GainXpTests {

        @Test
        @DisplayName("should increase XP when gaining positive amount")
        void shouldIncreaseXp() {
            user.gainXp(50);
            assertThat(user.getXp()).isEqualTo(50);
        }

        @Test
        @DisplayName("should level up when XP reaches threshold (Level 1 needs 100 XP)")
        void shouldLevelUpAtThreshold() {
            user.gainXp(100);
            assertThat(user.getLevel()).isEqualTo(2);
            assertThat(user.getXp()).isEqualTo(0);
        }

        @Test
        @DisplayName("should carry over excess XP after level up")
        void shouldCarryOverExcessXp() {
            user.gainXp(150);
            assertThat(user.getLevel()).isEqualTo(2);
            assertThat(user.getXp()).isEqualTo(50);
        }

        @Test
        @DisplayName("should level up multiple times if enough XP gained at once")
        void shouldLevelUpMultipleTimes() {
            user.gainXp(300);
            assertThat(user.getLevel()).isEqualTo(3);
        }

        @Test
        @DisplayName("should throw exception when gaining negative XP")
        void shouldThrowExceptionForNegativeXp() {
            assertThatThrownBy(() -> user.gainXp(-10))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("XP cannot be negative");
        }

        @Test
        @DisplayName("should not change state when gaining zero XP")
        void shouldHandleZeroXp() {
            user.gainXp(0);
            assertThat(user.getXp()).isEqualTo(0);
            assertThat(user.getLevel()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("getXpNeededForNextLevel()")
    class XpNeededTests {

        @Test
        @DisplayName("level 1 needs 100 XP to level up")
        void level1Needs100Xp() {
            assertThat(user.getXpNeededForNextLevel()).isEqualTo(100);
        }

        @Test
        @DisplayName("level 2 needs 200 XP to level up")
        void level2Needs200Xp() {
            user.gainXp(100); // now at level 2
            assertThat(user.getXpNeededForNextLevel()).isEqualTo(200);
        }
    }
}
