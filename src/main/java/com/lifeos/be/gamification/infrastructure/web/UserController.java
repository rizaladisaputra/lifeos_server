package com.lifeos.be.gamification.infrastructure.web;

import com.lifeos.be.auth.application.dto.AuthResponse;
import com.lifeos.be.gamification.domain.model.User;
import com.lifeos.be.gamification.domain.repository.UserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getMe() {
        User user = getCurrentUser();
        return ResponseEntity.ok(convertToAuthResponse(user));
    }

    @PostMapping("/fcm-token")
    public ResponseEntity<Void> registerFcmToken(@RequestBody FcmTokenRequest request) {
        User user = getCurrentUser();
        user.updateFcmToken(request.getToken()); // Rich domain method
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/xp")
    public ResponseEntity<AuthResponse> gainXp(@RequestBody XpGainRequest request) {
        User user = getCurrentUser();
        user.gainXp(request.getAmount()); // Rich domain method
        User saved = userRepository.save(user);
        return ResponseEntity.ok(convertToAuthResponse(saved));
    }

    private AuthResponse convertToAuthResponse(User user) {
        return AuthResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .xp(user.getXp())
                .level(user.getLevel())
                .build();
    }

    @Data
    public static class FcmTokenRequest {
        private String token;
    }

    @Data
    public static class XpGainRequest {
        private int amount;
    }
}
