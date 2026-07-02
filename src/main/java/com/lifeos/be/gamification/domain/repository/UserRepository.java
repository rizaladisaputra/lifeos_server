package com.lifeos.be.gamification.domain.repository;

import com.lifeos.be.gamification.domain.model.User;

import java.util.Optional;
import java.util.UUID;

/**
 * Domain Repository Port untuk User.
 */
public interface UserRepository {
    Optional<User> findByEmail(String email);
    Optional<User> findById(UUID id);
    boolean existsByEmail(String email);
    User save(User user);
}
