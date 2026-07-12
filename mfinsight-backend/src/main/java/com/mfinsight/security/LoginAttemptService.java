package com.mfinsight.security;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptService {
    private static final int MAX_ATTEMPTS = 5;
    private static final long BLOCK_SECONDS = 15 * 60;

    private record AttemptState(int failures, Instant blockedUntil) {}

    private final ConcurrentHashMap<String, AttemptState> attempts = new ConcurrentHashMap<>();

    public void assertLoginAllowed(String key) {
        AttemptState state = attempts.get(key);
        if (state == null || state.blockedUntil() == null) return;
        if (Instant.now().isAfter(state.blockedUntil())) {
            attempts.remove(key);
            return;
        }
        long remaining = state.blockedUntil().getEpochSecond() - Instant.now().getEpochSecond();
        throw new TooManyLoginAttemptsException(
                "Too many failed login attempts. Try again in " + Math.max(remaining, 1) + " seconds.");
    }

    public void recordFailure(String key) {
        attempts.compute(key, (k, state) -> {
            int failures = state == null ? 1 : state.failures() + 1;
            Instant blockedUntil = failures >= MAX_ATTEMPTS ? Instant.now().plusSeconds(BLOCK_SECONDS) : null;
            return new AttemptState(failures, blockedUntil);
        });
    }

    public void clear(String key) {
        attempts.remove(key);
    }
}

