package com.example.scanlink.api.authentication;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final ConcurrentHashMap<String, UserDto> users = new ConcurrentHashMap<>();

    @GetMapping("/ping")
    public Map<String, Object> ping() {
        return Map.of(
                "ok", true,
                "timestamp", Instant.now().toString()
        );
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            Authentication authentication,
            @RequestBody RegisterRequest request
    ) {
        if (authentication == null || !(authentication.getPrincipal() instanceof FirebaseUserPrincipal principal)) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        long now = System.currentTimeMillis();
        UserDto existing = users.get(principal.uid());

        UserDto userDto = existing != null ? existing : new UserDto();
        if (existing == null) {
            userDto.setUid(principal.uid());
            userDto.setEmail(principal.email());
            userDto.setRole("USER");
            userDto.setActive(true);
            userDto.setCreatedAt(now);
        }

        userDto.setDisplayName(request.getDisplayName());
        userDto.setDateOfBirth(request.getDateOfBirth());
        userDto.setGender(request.getGender());
        userDto.setUpdatedAt(now);

        users.put(principal.uid(), userDto);
        return ResponseEntity.ok(userDto);
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof FirebaseUserPrincipal principal)) {
            return ResponseEntity.status(401).body(Map.of("error", "Unauthorized"));
        }

        UserDto userDto = users.get(principal.uid());
        if (userDto == null) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        }

        return ResponseEntity.ok(userDto);
    }
}
